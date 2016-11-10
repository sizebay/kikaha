package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;

public interface SessionStore {

	Session createOrRetrieveSession( HttpServerExchange exchange, SessionIdManager sessionIdManager );
	void invalidateSession( Session session );
	void flush(Session currentSession);

	/**
	 * If no {@link Session} was not found at the storage, it will create a new one
	 * and store it for further usage. It was designed on a CAS style to avoid lock
	 * the register assigned to the {@code sessionId} for a long time.
	 *
	 * @param sessionId
	 * @param exchange
	 * @return
	 */
	default Session tryToCreateAndStoreNewSession(String sessionId, HttpServerExchange exchange, SessionIdManager sessionIdManager ){
		Session session = getSessionFromCache(sessionId);
		if (session == null)
			session = createAndStoreNewSession(sessionId, exchange, sessionIdManager);
		return session;
	}

	/**
	 * Retrieve the {@link Session}, identified by the {@code sessionId}, from the storage.
	 *
	 * @param sessionId
	 * @return
	 */
	Session getSessionFromCache(String sessionId);

	/**
	 * Create a new {@link Session} and store it at the main storage.
	 *
	 * @param sessionId
	 * @param exchange
	 * @return
	 */
	default Session createAndStoreNewSession(String sessionId, HttpServerExchange exchange, SessionIdManager sessionIdManager ){
		Session session = new DefaultSession( sessionId );
		storeSession(session.getId(), session);
		sessionIdManager.attachSessionId(exchange, session.getId());
		return session;
	}

	/**
	 * Store a {@link Session} at the storage.
	 *
	 * @param sessionId the identifier used to retrieve this same {@link Session}
	 * @param session
	 */
	void storeSession(String sessionId, Session session);
}
