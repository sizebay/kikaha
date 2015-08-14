package kikaha.core.security;

import io.undertow.server.HttpServerExchange;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
				if ( ( session = getSessionFromCache( sessionId ) ) == null ) {
					session = createNewSession();
					storeSession( session.getId(), session );
					attachSessionCookie( exchange, session.getId() );
				}
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

	protected Session createNewSession() {
		final String uuid = UUID.randomUUID().toString();
		return new DefaultSession( uuid );
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
