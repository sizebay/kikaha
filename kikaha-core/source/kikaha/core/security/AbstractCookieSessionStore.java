package kikaha.core.security;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;

public abstract class AbstractCookieSessionStore implements SessionStore {

	final String cookieName;

	public AbstractCookieSessionStore() {
		cookieName = "JSESSIONID";
	}

	protected String retrieveSessionIdFrom( HttpServerExchange exchange ) {
		final Cookie cookie = exchange.getRequestCookies().get( cookieName );
		return cookie != null ? cookie.getValue() : null;
	}

	protected void attachSessionCookie( HttpServerExchange exchange, String sessionId ) {
		final Cookie cookie = new CookieImpl( this.cookieName, sessionId ).setPath( "/" );
		exchange.setResponseCookie( cookie );
	}
}
