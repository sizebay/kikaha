package kikaha.hazelcast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import kikaha.core.security.Session;
import kikaha.hazelcast.config.HazelcastTestCase;

import org.junit.Test;

import trip.spi.Provided;

public class HazelcastSessionStoreTest extends HazelcastTestCase {

	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Provided
	HazelcastSessionStore store;

	@Test
	public void ensureThatIsAbleToCreateNewSessions(){
		final Session session = store.createOrRetrieveSession(exchange);
		assertNotNull(session);
	}

	@Test
	public void ensureThatRegisterACookieToTheNewSession(){
		final Session session = store.createOrRetrieveSession(exchange);
		assertNotNull(session);
		assertHasCookieForTheSession(session);
	}

	@Test
	public void ensureThatIsAbleToRetrieveAJustCreatedSession(){
		final Session session = store.createOrRetrieveSession(exchange);
		assertNotNull(session);
		assertHasCookieForTheSession(session);
		defineResponseCookiesAsRequestCookiesForTestPropose();
		store.createOrRetrieveSession(exchange);
		assertEquals( store.retrieveAllSessions().size(), 1 );
	}

	private void defineResponseCookiesAsRequestCookiesForTestPropose() {
		for ( final Cookie cookie : exchange.getResponseCookies().values() )
			exchange.getRequestCookies().put(cookie.getName(), cookie);
	}

	private void assertHasCookieForTheSession(final Session session) {
		final Cookie cookie = exchange.getResponseCookies().get("JSESSIONID");
		assertNotNull( cookie );
		assertEquals( session.getId(), cookie.getValue() );
	}
}
