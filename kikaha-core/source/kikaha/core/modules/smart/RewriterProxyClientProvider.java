package kikaha.core.modules.smart;

import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.UndertowClient;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.proxy.ProxyCallback;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.ProxyConnection;
import io.undertow.util.AttachmentKey;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.val;

import org.xnio.IoUtils;
import org.xnio.OptionMap;

/**
 * Simple proxy client provider. This provider simply proxies to another server,
 * using a a one to one connection strategy.<br>
 * <br>
 * <b>Note:</b> this proxy client was based on Stuart Douglas' proxy client
 * available as
 * {@code io.undertow.server.handlers.proxy.SimpleProxyClientProvider}.
 *
 * @author Stuart Douglas
 * @author Miere Teixeira
 */
@RequiredArgsConstructor
public class RewriterProxyClientProvider implements ProxyClient {

	static final ProxyTarget TARGET = new ProxyTarget() {};

	final AttachmentKey<ClientConnection> clientAttachmentKey = AttachmentKey.create( ClientConnection.class );
	final UndertowClient client = UndertowClient.getInstance();
	final RequestMatcher requestMatcher;
	final String targetPath;

	@Override
	public ProxyTarget findTarget( final HttpServerExchange exchange )
	{
		val properties = new HashMap<String, String>();
		if ( !requestMatcher.apply( exchange, properties ) )
			return null;
		return TARGET;
	}

	@Override
	// UNCHECKED: more than 4 parameters because it implements a superinterface
	public void getConnection( final ProxyTarget target, final HttpServerExchange exchange,
		final ProxyCallback<ProxyConnection> callback,
		final long timeout, final TimeUnit timeUnit )
	// CHECKED
	{
		try {
			getConnection( exchange, callback );
		} catch ( final URISyntaxException e ) {
			throw new RuntimeException( e );
		}
	}

	void getConnection( final HttpServerExchange exchange, final ProxyCallback<ProxyConnection> callback ) throws URISyntaxException
	{
		final ClientConnection existing = exchange.getConnection().getAttachment( clientAttachmentKey );
		if ( existing != null )
			if ( existing.isOpen() ) {
				callback.completed( exchange, new ProxyConnection( existing, targetPath ) );
				return;
			} else
				exchange.getConnection().removeAttachment( clientAttachmentKey );
		client.connect(
			new ConnectNotifier( callback, exchange ), new URI( targetPath ),
			exchange.getIoThread(), exchange.getConnection().getByteBufferPool(), OptionMap.EMPTY );
	}

	public static ProxyClient from( final SmartRouteRule rule )
	{
		return new RewriterProxyClientProvider(
			DefaultMatcher.from( rule ),
			rule.target() );
	}

	@RequiredArgsConstructor
	private final class ConnectNotifier implements ClientCallback<ClientConnection> {

		final ProxyCallback<ProxyConnection> callback;
		final HttpServerExchange exchange;

		@Override
		public void completed( final ClientConnection connection )
		{
			final ServerConnection serverConnection = exchange.getConnection();
			serverConnection.putAttachment( clientAttachmentKey, connection );
			serverConnection.addCloseListener( serverConnection1 -> IoUtils.safeClose( connection ) );
			connection.getCloseSetter().set( channel -> serverConnection.removeAttachment( clientAttachmentKey ) );
			callback.completed( exchange, new ProxyConnection( connection, targetPath ) );
		}

		@Override
		public void failed( final IOException e )
		{
			callback.failed( exchange );
		}
	}
}