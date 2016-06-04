package kikaha.core.modules.websocket;

import java.security.Principal;
import java.util.*;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import kikaha.core.url.URLMatcher;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class WebSocketSession {

	final WebSocketHttpExchange originalExchange;
	final Map<String, List<String>> requestHeaders;
	final Map<String, List<String>> responseHeaders;
	final Map<String, String> requestParameters;
	final URLMatcher urlMatcher;
	final String requestURI;
	final Principal userPrincipal;
	final WebSocketChannel channel;
	final Iterable<WebSocketChannel> peerConnections;

	public WebSocketSession( final WebSocketHttpExchange originalExchange, final WebSocketChannel channel, final URLMatcher urlMatcher ) {
		this.originalExchange = originalExchange;
		this.urlMatcher = urlMatcher;
		this.channel = channel;
		this.requestHeaders = originalExchange.getRequestHeaders();
		this.responseHeaders = originalExchange.getResponseHeaders();
		this.userPrincipal = originalExchange.getUserPrincipal();
		this.requestURI = channel.getUrl();
		this.peerConnections = retrievePeerConnectionsForCurrentURLRequest( channel );
		this.requestParameters = extractRequestParameters( channel );
	}

	Map<String, String> extractRequestParameters( final WebSocketChannel channel ) {
		final String url = channel.getUrl();
		final Map<String, String> foundParameters = new HashMap<>();
		if ( !urlMatcher.matches( url, foundParameters ) )
			throw new UnsupportedOperationException( "There was a huge mistake on implementation: it doesn't matched URL: " + url );
		return foundParameters;
	}

	/**
	 * Create a new {@link WebSocketSession} instance holding only
	 * {@code peerConnections} related to {@link WebSocketChannel#getUrl()}
	 * data.
	 *
	 * @param channel
	 * @return
	 */
	public WebSocketSession channel( final WebSocketChannel channel ) {
		final List<WebSocketChannel> hashSet = retrievePeerConnectionsForCurrentURLRequest( channel );
		return cloneWith( channel, channel.getUrl(), hashSet );
	}

	List<WebSocketChannel> retrievePeerConnectionsForCurrentURLRequest( final WebSocketChannel channel ) {
		final List<WebSocketChannel> hashSet = new ArrayList<>();
		for ( final WebSocketChannel peer : channel.getPeerConnections() )
			if ( channel.getUrl().equals( peer.getUrl() ) )
				hashSet.add( peer );
		return hashSet;
	}

	WebSocketSession cloneWith( final WebSocketChannel channel, final String requestURI, final Iterable<WebSocketChannel> peerConnections ) {
		final Map<String, String> requestParameters = extractRequestParameters( channel );
		return new WebSocketSession(
			null, requestHeaders, null, requestParameters, urlMatcher, requestURI,
			userPrincipal, channel, peerConnections );
	}

	/**
	 * Prepare to send a message to someone.
	 *
	 * @param message
	 * @return
	 */
	public Sender send( final String message ) {
		return new Sender( message );
	}

	/**
	 * Send a message to all Peer Connections related to current
	 * {@code requestURI}.
	 *
	 * @param message
	 */
	public void broadcast( final String message ) {
		for ( final WebSocketChannel peer : peerConnections() )
			WebSockets.sendText( message, peer, null );
	}

	@RequiredArgsConstructor
	public class Sender {
		final String message;

		/**
		 * Send the prepared message to respective {@code peers}.
		 *
		 * @param peers
		 */
		public void to( final WebSocketChannel... peers ) {
			for ( final WebSocketChannel peer : peers )
				WebSockets.sendText( message, peer, null );
		}
	}
}