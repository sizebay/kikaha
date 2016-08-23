package kikaha.core.modules.smart;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import io.undertow.client.UndertowClient;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.proxy.*;
import kikaha.core.url.URLMatcher;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class ReverseProxyClientProvider implements ProxyClient {

	final Map<String, Host> clientPerHost = new ConcurrentHashMap<>();
	final UndertowClient client = UndertowClient.getInstance();
	final RequestMatcher requestMatcher;
	final URLMatcher targetPath;

	/**
	 * Time in seconds between retries for problem servers
	 */
	private volatile int problemServerRetry = 10;

	/**
	 * The number of connections to create per thread
	 */
	private volatile int connectionsPerThread = 10;

	private volatile int maxQueueSize = 0;
	private volatile int softMaxConnectionsPerThread = 5;
	private volatile int ttl = -1;

	@Override
	public ProxyTarget findTarget( final HttpServerExchange exchange )
	{
		final Map<String, String> properties = new HashMap<>();
		if ( !requestMatcher.apply( exchange, properties ) )
			return null;
		return SinglePathProxyTarget.to( targetPath.replace( properties ) );
	}

	@Override
	// UNCHECKED: more than 4 parameters because it implements a superinterface
	public void getConnection( final ProxyTarget target, final HttpServerExchange exchange,
		final ProxyCallback<ProxyConnection> callback,
		final long timeout, final TimeUnit timeUnit )
	// CHECKED
	{
		try {
			final String targetUrl = ( (SinglePathProxyTarget)target ).target;
			final URI uri = new URI( targetUrl );
			fixExchangeURI( exchange, uri );
			getClientPerHost( createTargetURIFrom( uri ) )
				.connectionPool
				.connect(target, exchange, callback, timeout, timeUnit, false);
		} catch ( final Exception e ) {
			e.printStackTrace();
			throw new RuntimeException( e );
		}
	}

	static void fixExchangeURI( final HttpServerExchange exchange, final URI uri ){
		final String relativePath = uri.getPath() == null ? "/" : uri.getPath();
		exchange.setRelativePath( relativePath );
		exchange.setRequestPath( relativePath );
		exchange.setResolvedPath( relativePath );
		exchange.setRequestURI( uri.toString() );
	}

	static URI createTargetURIFrom( URI uri ) {
		try {
			final StringBuilder buffer =
				new StringBuilder( uri.getScheme() )
				.append("://").append( uri.getAuthority() ).append('/');
			return new URI( buffer.toString() );
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	private Host getClientPerHost( URI uri ) {
		return clientPerHost.computeIfAbsent( uri.getAuthority(), k-> new Host( uri, OptionMap.EMPTY ) );
	}

	public static ProxyClient from( final SmartRouteRule rule )
	{
		return new ReverseProxyClientProvider(
				DefaultMatcher.from( rule ),
				URLMatcher.compile( rule.target() ) );
	}

	public final class Host extends ConnectionPoolErrorHandler.SimpleConnectionPoolErrorHandler implements ConnectionPoolManager {
		final ProxyConnectionPool connectionPool;

		private Host( final URI uri, final OptionMap options) {
			this.connectionPool = new ProxyConnectionPool(this, null, uri, null, client, options);
		}

		@Override
		public int getProblemServerRetry() {
			return problemServerRetry;
		}

		@Override
		public int getMaxConnections() {
			return connectionsPerThread;
		}

		@Override
		public int getMaxCachedConnections() {
			return connectionsPerThread;
		}

		@Override
		public int getSMaxConnections() {
			return softMaxConnectionsPerThread;
		}

		@Override
		public long getTtl() {
			return ttl;
		}

		@Override
		public int getMaxQueueSize() {
			return maxQueueSize;
		}
	}
}

@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "to")
class SinglePathProxyTarget implements ProxyClient.ProxyTarget {
	final String target;
}