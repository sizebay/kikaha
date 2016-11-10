package kikaha.core.modules.security;

import java.util.function.Supplier;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.*;
import lombok.RequiredArgsConstructor;

/**
 * A helper class to retrieve and store cookies into the {@link HttpServerExchange}.
 */
@RequiredArgsConstructor
public class SessionCookie implements SessionIdManager {

	final String cookieName;

	/**
	 * Constructs a SessionCookie named "JSESSIONID".
	 */
	public SessionCookie(){
		cookieName = "JSESSIONID";
	}

	/**
	 * Attach a session cookie, identified by {@code sessionId}, into the current request.
	 *
	 * @param exchange
	 * @param sessionId
	 */
	@Override
	public void attachSessionId(HttpServerExchange exchange, String sessionId ) {
		final Cookie cookie = new CookieImpl( this.cookieName, sessionId ).setPath( "/" );
		exchange.setResponseCookie( cookie );
	}

	/**
	 * Extract the session id from the current request.
	 * @param exchange
	 * @param sessionIdCreator
	 * @return
	 */
	@Override
	public String retrieveSessionIdFrom(HttpServerExchange exchange, Supplier<String> sessionIdCreator ) {
		final Cookie cookie = exchange.getRequestCookies().get( cookieName );
		return cookie != null ? cookie.getValue() : sessionIdCreator.get();
	}
}
