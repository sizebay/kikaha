package kikaha.core.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import kikaha.core.HttpServerExchangeStub;

import org.junit.Before;
import org.junit.Test;

import trip.spi.DefaultServiceProvider;
import trip.spi.Provided;

public class InMemorySessionStoreTest {

	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Provided
	InMemorySessionStore store;

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
		final Session retrievedSession = store.createOrRetrieveSession(exchange);
		assertEquals( store.retrieveAllSessions().size(), 1 );
		assertSame(session, retrievedSession);
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

	@Before
	public void provideDependencies(){
		new DefaultServiceProvider().provideOn(this);
	}
}
