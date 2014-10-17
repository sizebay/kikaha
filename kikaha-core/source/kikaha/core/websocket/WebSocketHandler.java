package kikaha.core.websocket;

import io.undertow.websockets.core.CloseMessage;

import java.io.IOException;

public interface WebSocketHandler {

	public void onOpen( final WebSocketSession session );

	public void onText( final WebSocketSession session, final String message )
		throws IOException;

	public void onClose( final WebSocketSession session, final CloseMessage cm );
}
