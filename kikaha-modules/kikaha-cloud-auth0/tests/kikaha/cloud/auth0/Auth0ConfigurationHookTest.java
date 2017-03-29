package kikaha.cloud.auth0;

import static kikaha.cloud.auth0.Auth0Authentication.NONCE;
import static kikaha.core.test.HttpServerExchangeStub.createAuthenticatedHttpExchange;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;
import java.util.Map;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.security.*;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for Auth0ConfigurationHook.
 */
@RunWith( KikahaRunner.class )
public class Auth0ConfigurationHookTest {

	@Inject Auth0ConfigurationHook hook;

	@Test
	public void ensureCanReadExtraProperties(){
		final Map<String, Object> extraParameters = hook.getExtraParameters();
		assertEquals( "bad-client-id", extraParameters.get( "clientId" ) );
		assertEquals( "localhost", extraParameters.get( "clientDomain" ) );
		assertEquals( "/auth/callback", extraParameters.get( "authenticationCallbackUrl" ) );
	}

	@Test
	public void ensureConfiguredRequestAsExpected(){
		final HttpServerExchange httpExchange = createAuthenticatedHttpExchange();
		final Session currentSession = ( (SecurityContext) httpExchange.getSecurityContext() ).getCurrentSession();
		hook.configure( httpExchange, currentSession );
		verify( currentSession ).setAttribute( eq(NONCE), eq("") );
	}
}