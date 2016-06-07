package kikaha.urouting.samples;

import kikaha.urouting.api.*;
import lombok.ToString;

/**
 *
 */
@WebSocket( "non-string-chat" )
public class NonStringChatResource {

	@OnMessage
	public void onMessage(
			@PathParam("id") final Long id,
			final NonStringMessage message ) {
		System.out.println( id + ":" + message );
	}

	@ToString
	public static class NonStringMessage { }
}
