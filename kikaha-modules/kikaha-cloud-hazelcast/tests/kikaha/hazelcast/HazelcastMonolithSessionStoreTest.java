package kikaha.hazelcast;

import com.hazelcast.core.IMap;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.security.Session;
import kikaha.core.modules.security.SessionCookie;
import kikaha.core.test.HttpServerExchangeStub;
import kikaha.core.test.KikahaRunner;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Named;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link HazelcastMonolithSessionStore}.
 * Created by miere.teixeira on 27/07/2017.
 */
@RunWith(KikahaRunner.class)
public class HazelcastMonolithSessionStoreTest {

    @Inject
    @Named("session-cache")
    IMap<String, Session> sessionCache;

    @Inject HazelcastMonolithSessionStoreImpl sessionStore;

    @Test
    public void consegueObterSessionParaUI(){
        val sessionIdManager = new SessionCookie();
        val exchange = HttpServerExchangeStub.createHttpExchange();
        val session = sessionStore.createOrRetrieveSession( exchange, sessionIdManager );
        assertNotNull( session );
        assertNotEquals( HazelcastMonolithSessionStore.SESSION_ID, session.getId() );
    }

    @Test
    public void sessionParaUIéSalvaNaCacheDoHazelcast(){
        val sessionIdManager = new SessionCookie();
        val exchange = HttpServerExchangeStub.createHttpExchange();
        val session = sessionStore.createOrRetrieveSession( exchange, sessionIdManager );
        val foundSession = sessionCache.get( session.getId() );
        assertNotNull( foundSession );
        assertEquals( session, foundSession );
    }

    @Test
    public void nãoBuscaSessionParaAPI(){
        val sessionIdManager = new SessionCookie();
        val exchange = HttpServerExchangeStub.createHttpExchange();
        exchange.setRelativePath( "/api/users" );
        val session = sessionStore.createOrRetrieveSession( exchange, sessionIdManager );
        assertNotNull( session );
        assertEquals( HazelcastMonolithSessionStore.SESSION_ID, session.getId() );
    }

    @Test
    public void sessionParaAPInãoÉSalvaNaCache(){
        val sessionIdManager = new SessionCookie();
        val exchange = HttpServerExchangeStub.createHttpExchange();
        exchange.setRelativePath( "/api/users" );
        val session = sessionStore.createOrRetrieveSession( exchange, sessionIdManager );
        val foundSession = sessionCache.get( session.getId() );
        assertNull( foundSession );
    }
}

class HazelcastMonolithSessionStoreImpl extends HazelcastMonolithSessionStore {

    @Override
    protected boolean shouldNotStoreSession(HttpServerExchange exchange) {
        return exchange.getRelativePath().startsWith( "/api" );
    }
}