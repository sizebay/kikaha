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
		handler.onOpen( channel, exchange );
		channel.getReceiveSetter().set( new DelegatedReceiveListener( handler ) );
		channel.resumeReceives();
	}
}

@RequiredArgsConstructor
class DelegatedReceiveListener extends AbstractReceiveListener {

	final WebSocketHandler handler;

	@Override
	public void onFullTextMessage( final WebSocketChannel channel, final BufferedTextMessage message ) throws IOException {
		handler.onText( channel, message );
	}

	@Override
	protected void onCloseMessage( final CloseMessage cm, final WebSocketChannel channel ) {
		handler.onClose( channel, cm );
	}
}