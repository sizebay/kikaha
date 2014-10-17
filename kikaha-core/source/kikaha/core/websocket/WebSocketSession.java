package kikaha.core.websocket;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors( fluent = true )
public class WebSocketSession {

	WebSocketHttpExchange originalExchange;
	Map<String, List<String>> requestHeaders;
	Map<String, List<String>> responseHeaders;
	Map<String, List<String>> requestParameters;
	Principal userPrincipal;
	WebSocketChannel channel;

	public WebSocketSession( final WebSocketHttpExchange originalExchange ) {
		this.originalExchange = originalExchange;
		this.requestHeaders = originalExchange.getRequestHeaders();
		this.responseHeaders = originalExchange.getResponseHeaders();
		this.requestParameters = originalExchange.getRequestParameters();
		this.userPrincipal = originalExchange.getUserPrincipal();
	}

	public Set<WebSocketChannel> peerConnections() {
		return channel.getPeerConnections();
	}

	void clean() {
		this.originalExchange = null;
		this.channel = null;
		this.responseHeaders = null;
	}

	public Sender send( final String message ) {
		return new Sender( message );
	}

	public void broadcast( final String message ) {
		for ( final WebSocketChannel peer : peerConnections() )
			WebSockets.sendText( message, peer, null );
	}

	@RequiredArgsConstructor
	public class Sender {
		final String message;

		public void to( final WebSocketChannel peer ) {
			WebSockets.sendText( message, peer, null );
		}
	}
}