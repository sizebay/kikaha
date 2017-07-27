package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;

/**
 * A {@link kikaha.core.modules.security.SessionStore} designed to not store sessions
 * regarding API requests.
 *
 * Created by miere.teixeira on 27/07/2017.
 */
public abstract class MonolithSessionStore implements SessionStore {

    public static final String SESSION_ID = "SESSION-ID-API";

    @lombok.experimental.Delegate(excludes = NotDelegatedMethods.class)
    protected abstract SessionStore getSessionStore();

    @Override
    public Session createOrRetrieveSession(HttpServerExchange exchange, SessionIdManager sessionIdManager) {
        final String sessionId = sessionIdManager.retrieveSessionIdFrom( exchange );
        Session session = getSessionFromCache( sessionId );

        if ( session == null && shouldNotStoreSession( exchange ) )
            return new DefaultSession(SESSION_ID);
        else if ( session == null )
            session = getSessionStore().createOrRetrieveSession(exchange, sessionIdManager);

        return session;
    }

    protected abstract boolean shouldNotStoreSession(HttpServerExchange exchange);

    @Override
    public void storeSession(String s, Session session) {
        if ( !session.getId().equals( SESSION_ID ) )
            getSessionStore().storeSession(s, session);
    }

    private interface NotDelegatedMethods {
        Session createOrRetrieveSession(HttpServerExchange exchange, SessionIdManager sessionIdManager);
        void storeSession(String s, Session session);
    }
}
