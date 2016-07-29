package kikaha.core.modules.security;

import java.util.*;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InMemorySessionStore extends AbstractCookieSessionStore {

	final Map<String, Session> cache = new HashMap<>();

	@Override
	public Session createOrRetrieveSession( HttpServerExchange exchange ) {
		final String sessionId = retrieveSessionIdFrom( exchange );
		Session session = getSessionFromCache( sessionId );
		if ( session == null )
			synchronized ( cache ) {
				session = tryToCreateAndStoreNewSession(sessionId, exchange);
			}
		return session;
	}

	protected void storeSession(final String sessionId, Session session) {
		cache.put( sessionId, session );
	}

	protected Session getSessionFromCache( String sessionId ) {
		if ( sessionId == null )
			return null;
		return cache.get( sessionId );
	}

	@Override
	public void invalidateSession( Session session ) {
		cache.remove( session.getId() );
	}

	@Override
	public void flush(Session currentSession) {
	}

	public Collection<Session> retrieveAllSessions() {
		return cache.values();
	}
}
