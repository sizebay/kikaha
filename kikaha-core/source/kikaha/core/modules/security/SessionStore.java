package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;

public interface SessionStore {

	Session createOrRetrieveSession( HttpServerExchange exchange );
	void invalidateSession( Session session );
	void flush(Session currentSession);

}
