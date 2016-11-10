package kikaha.core.modules.security;

import java.util.UUID;

import io.undertow.server.HttpServerExchange;

public class EmptySessionStore implements SessionStore {

	private static final String SESSION_ID = UUID.randomUUID().toString();
	private final Session session = new DefaultSession( SESSION_ID );

	@Override
	public Session createOrRetrieveSession( HttpServerExchange exchange, SessionIdManager sessionIdManager ) {
		return session;
	}

	@Override
	public void invalidateSession( Session session ) {}

	@Override
	public void flush( Session currentSession ) {}

	@Override
	public Session getSessionFromCache(String sessionId) {
		throw new UnsupportedOperationException("getSessionFromCache not implemented yet!");
	}

	@Override
	public void storeSession(String sessionId, Session session) {
		throw new UnsupportedOperationException("storeSession not implemented yet!");
	}
}
