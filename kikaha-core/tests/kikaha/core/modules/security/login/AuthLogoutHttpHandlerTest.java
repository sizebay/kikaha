package kikaha.core.modules.security.login;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import kikaha.core.test.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for AuthLogoutHttpHandler.
 */
@RunWith( KikahaRunner.class )
public class AuthLogoutHttpHandlerTest {

	@Inject AuthLogoutHttpHandler handler;

	@Test
	public void ensureCanLogoutWhenLoggedIn() throws Exception {
		final HttpServerExchange exchange = HttpServerExchangeStub.createAuthenticatedHttpExchange();
		handler.handleRequest( exchange );
		assertEquals( 303, exchange.getStatusCode() );
		assertEquals( "/auth/", exchange.getResponseHeaders().getFirst( Headers.LOCATION ) );
	}

	@Test
	public void ensureCanNotLogoutWhenUserHaveNotPreviouslyAuthenticated() throws Exception {
		final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
		handler.handleRequest( exchange );
		assertEquals( 500, exchange.getStatusCode() );
		verify( exchange.getResponseSender() ).send( eq(AuthLogoutHttpHandler.NOT_LOGGED_IN) );
	}
}