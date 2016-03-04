package kikaha.core.security;

import java.util.UUID;

import io.undertow.server.HttpServerExchange;

public class EmptySessionStore implements SessionStore {

	private static final String SESSION_ID = UUID.randomUUID().toString();
	private final Session session = new DefaultSession( SESSION_ID );

	@Override
	public Session createOrRetrieveSession( HttpServerExchange exchange ) {
		return session;
	}

	@Override
	public void invalidateSession( Session session ) {}

	@Override
	public void flush( Session currentSession ) {}
}
