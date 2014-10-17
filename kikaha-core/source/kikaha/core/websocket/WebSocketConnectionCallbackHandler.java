package kikaha.core.websocket;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebSocketConnectionCallbackHandler
	implements WebSocketConnectionCallback {

	final WebSocketHandler handler;

	@Override
	public void onConnect( final WebSocketHttpExchange exchange, final WebSocketChannel channel ) {
		final WebSocketSession session = createSession( exchange, channel );
		handler.onOpen( session );
		session.clean();
		channel.getReceiveSetter().set( createListener( session ) );
		channel.resumeReceives();
	}

	public DelegatedReceiveListener createListener( final WebSocketSession session ) {
		return new DelegatedReceiveListener( handler, session );
	}

	public WebSocketSession createSession( final WebSocketHttpExchange exchange, final WebSocketChannel channel ) {
		return new WebSocketSession( exchange ).channel( channel );
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