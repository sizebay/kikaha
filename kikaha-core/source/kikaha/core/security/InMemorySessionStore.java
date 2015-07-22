package kikaha.core.security;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InMemorySessionStore implements SessionStore {

	final Map<String, Session> cache = new HashMap<>();
	final String cookieName;

	public InMemorySessionStore() {
		cookieName = "JSESSIONID";
	}

	@Override
	public Session createOrRetrieveSession( HttpServerExchange exchange ) {
		final String sessionId = retrieveSessionIdFrom( exchange );
		Session session = getSessionFromCache( sessionId );
		if ( session == null )
			synchronized ( cache ) {
				if ( ( session = getSessionFromCache( sessionId ) ) == null ) {
					cache.put( sessionId, session = createNewSession() );
					attachSessionCookie( exchange, session.getId() );
				}
			}
		return session;
	}

	protected Session getSessionFromCache( String sessionId ) {
		if ( sessionId == null )
			return null;
		return cache.get( sessionId );
	}

	protected Session createNewSession() {
		final String uuid = UUID.randomUUID().toString();
		return new DefaultSession( uuid );
	}

	protected String retrieveSessionIdFrom( HttpServerExchange exchange ) {
		final Cookie cookie = exchange.getRequestCookies().get( cookieName );
		return cookie != null ? cookie.getValue() : null;
	}

	protected void attachSessionCookie( HttpServerExchange exchange, String sessionId ) {
		final Cookie cookie = new CookieImpl( this.cookieName, sessionId ).setPath( "/" );
		exchange.setResponseCookie( cookie );
	}

	@Override
	public void invalidateSession( Session session ) {
		cache.remove( session.getId() );
	}

	@Override
	public void flush(Session currentSession) {
	}
}
