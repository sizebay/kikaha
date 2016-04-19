package kikaha.urouting.samples;

import io.undertow.websockets.core.CloseMessage;
import kikaha.core.modules.websocket.WebSocketSession;
import kikaha.urouting.api.HeaderParam;
import kikaha.urouting.api.OnClose;
import kikaha.urouting.api.OnError;
import kikaha.urouting.api.OnMessage;
import kikaha.urouting.api.OnOpen;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.WebSocket;

@WebSocket( "chat" )
public class ChatResource {

	@OnMessage
	public void onMessage(
		@PathParam("id") final Long id,
		final String message ) {
		System.out.println( id + ":" + message );
	}

	@OnOpen
	public void onOpen(
		@HeaderParam( "SESSION" ) final String sessionId,
		final WebSocketSession session ) {
		session.broadcast( sessionId );
	}

	@OnError
	public void onError( final Throwable cause ) {
		System.err.println( cause.getMessage() );
	}

	@OnClose
	public void onClose( final CloseMessage cm ) {
		System.out.println( cm.getReason() );
	}
}
