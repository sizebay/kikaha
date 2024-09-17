package kikaha.urouting.it.websocket;

import javax.inject.Singleton;
import kikaha.core.modules.websocket.WebSocketSession;
import kikaha.urouting.api.*;

/**
 *
 */
@Singleton
@WebSocket( "it/websocket" )
public class SampleWebSocketResource {

	static final String HELLO_WORLD = "Hello WORLD!";

	volatile boolean opened = false;
	volatile String message = null;

	@OnOpen
	public void onOpen(){
		opened = true;
	}

	@OnMessage
	public void onMessage( WebSocketSession session, String message ) {
		this.message = message;
		session.broadcast( HELLO_WORLD );
	}
}
