package kikaha.core.modules.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.Arrays;

import kikaha.core.test.HttpServerExchangeStub;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BasicAuthenticationMechanismTest {

	final static String AUTHORIZATION = "Basic dXNlcm5hbWU6cGFzc3dvcmQ=";
	static final Credential CREDENTIAL = new UsernameAndPasswordCredential("username","password");
	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Mock IdentityManager identityManager;
	@Mock Session session;
	@Mock Account account;

	@Test
	public void ensureThatIsAbleToSendCorrectCredentialsToIdentityManagerWhenHeaderIsPresent(){
		doReturn(account).when(identityManager).verify( eq(CREDENTIAL) );
		exchange.getRequestHeaders().put(Headers.AUTHORIZATION, AUTHORIZATION);
		final AuthenticationMechanism mechanism = new BasicAuthenticationMechanism();
		final Account authenticated = mechanism.authenticate(exchange, Arrays.asList(identityManager), session);
		assertNotNull(authenticated);
	}

	@Test
	public void ensureThatDoNotSendCredentialsToIdentityManagerWhenNoHeaderIsPresent(){
		doReturn(account).when(identityManager).verify( any() );
		final AuthenticationMechanism mechanism = new BasicAuthenticationMechanism();
		final Account authenticated = mechanism.authenticate(exchange, Arrays.asList(identityManager), session);
		assertNull(authenticated);
		verify( identityManager, never() ).verify( any() );
	}

	@Test
	public void ensureThatDoNotSendCredentialsToIdentityManagerWhenInvalidHeaderIsPresent(){
		doReturn(account).when(identityManager).verify( any() );
		exchange.getRequestHeaders().put(Headers.AUTHORIZATION, AUTHORIZATION.toUpperCase());
		final AuthenticationMechanism mechanism = new BasicAuthenticationMechanism();
		final Account authenticated = mechanism.authenticate(exchange, Arrays.asList(identityManager), session);
		assertNull(authenticated);
	}

	@Test
	public void ensureThatIsAbleToSentCorrectChallengeData(){
		final AuthenticationMechanism mechanism = new BasicAuthenticationMechanism();
		mechanism.sendAuthenticationChallenge(exchange, session);
		assertEquals(exchange.getResponseCode(), 401);
		final String header = exchange.getResponseHeaders().get( Headers.WWW_AUTHENTICATE ).getFirst();
		assertEquals(header, "Basic realm=\"default\"");
	}
}
