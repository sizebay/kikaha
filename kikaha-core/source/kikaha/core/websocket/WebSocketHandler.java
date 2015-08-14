package kikaha.core.websocket;

import io.undertow.websockets.core.CloseMessage;

import java.io.IOException;

public interface WebSocketHandler {

	void onOpen( final WebSocketSession session );

	void onText( final WebSocketSession session, final String message ) throws IOException;

	void onClose( final WebSocketSession session, final CloseMessage cm );
}
