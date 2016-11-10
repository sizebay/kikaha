package kikaha.core.modules.security;

import java.util.*;
import io.undertow.server.HttpServerExchange;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InMemorySessionStore implements SessionStore {

	static final String MSG_NOT_PRODUCTION_READY = "The InMemorySessionStore isn't designed for production proposes. Please consider using the HazelcastSecuritySessionStore.";
	final Map<String, Session> cache = new HashMap<>();

	public InMemorySessionStore(){
		log.info( MSG_NOT_PRODUCTION_READY );
	}

	@Override
	public Session createOrRetrieveSession( HttpServerExchange exchange, SessionIdManager sessionIdManager ) {
		final String sessionId = sessionIdManager.retrieveSessionIdFrom( exchange );
		Session session = getSessionFromCache( sessionId );
		if ( session == null )
			synchronized ( cache ) {
				session = tryToCreateAndStoreNewSession(sessionId, exchange, sessionIdManager);
			}
		return session;
	}

	public void storeSession(final String sessionId, Session session) {
		cache.put( sessionId, session );
	}

	public Session getSessionFromCache( String sessionId ) {
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
