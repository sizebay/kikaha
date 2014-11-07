package kikaha.core.websocket;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.io.IOException;

import kikaha.core.url.URLMatcher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebSocketConnectionCallbackHandler
	implements WebSocketConnectionCallback {

	final WebSocketHandler handler;
	final URLMatcher urlMatcher;

	@Override
	public void onConnect( final WebSocketHttpExchange exchange, final WebSocketChannel channel ) {
		final WebSocketSession session = createSession( exchange, channel );
		handler.onOpen( session );
		channel.getReceiveSetter().set( createListener( session ) );
		channel.resumeReceives();
	}

	public WebSocketSession createSession( final WebSocketHttpExchange exchange, final WebSocketChannel channel ) {
		return new WebSocketSession( exchange, channel, urlMatcher );
	}

	public DelegatedReceiveListener createListener( final WebSocketSession session ) {
		return new DelegatedReceiveListener( handler, session );
	}
}

@RequiredArgsConstructor
class DelegatedReceiveListener extends AbstractReceiveListener {

	final WebSocketHandler handler;
	final WebSocketSession session;

	@Override
	public void onFullTextMessage( final WebSocketChannel channel, final BufferedTextMessage message ) throws IOException {
		handler.onText( session.channel( channel ), message.getData() );
	}

	@Override
	protected void onCloseMessage( final CloseMessage cm, final WebSocketChannel channel ) {
		handler.onClose( session.channel( channel ), cm );
	}
}