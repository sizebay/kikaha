package kikaha.hazelcast;

import java.util.Collection;
import javax.inject.*;
import com.hazelcast.core.*;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.security.*;

@Singleton
public class HazelcastSessionStore implements SessionStore {

	@Inject
	@Named( "session-cache" )
	IMap<String, Session> sessionCache;

	@Override
	public Session createOrRetrieveSession( HttpServerExchange exchange, SessionIdManager sessionIdManager ) {
		final String sessionId = sessionIdManager.retrieveSessionIdFrom( exchange );
		Session session = getSessionFromCache( sessionId );
		if ( session == null ) {
			sessionCache.lock(sessionId);
			try { session = tryToCreateAndStoreNewSession(sessionId, exchange, sessionIdManager); }
			finally { sessionCache.unlock(sessionId); }
		}
		return session;
	}

	@Override
	public void storeSession(String sessionId, Session session) {
		sessionCache.set(sessionId, session);
	}

	@Override
	public Session getSessionFromCache( String sessionId ) {
		if ( sessionId == null )
			return null;
		return sessionCache.get( sessionId );
	}

	@Override
	public void invalidateSession(Session session) {
		sessionCache.remove(session.getId());
	}

	@Override
	public void flush(Session session) {
		sessionCache.set(session.getId(), session);
	}

	public Collection<Session> retrieveAllSessions(){
		return sessionCache.values();
	}
}
