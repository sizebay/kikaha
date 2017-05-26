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

import kikaha.config.Config;
import kikaha.core.test.HttpServerExchangeStub;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.enterprise.inject.Model;

@RunWith(MockitoJUnitRunner.class)
public class BasicAuthenticationMechanismTest {

	final static String AUTHORIZATION = "Basic dXNlcm5hbWU6cGFzc3dvcmQ=";
	static final Credential CREDENTIAL = new UsernameAndPasswordCredential("username","password");
	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Spy @InjectMocks BasicAuthenticationMechanism mechanism;

	@Mock IdentityManager identityManager;
	@Mock Session session;
	@Mock Account account;
	@Mock Config config;

	@Before
	public void defineApplicationName(){
		doReturn("default").when( config ).getString( eq("server.smart-server.application.name") );
		mechanism.defineARealm();
	}

	@Test
	public void ensureThatIsAbleToSendCorrectCredentialsToIdentityManagerWhenHeaderIsPresent(){
		doReturn(account).when(identityManager).verify( eq(CREDENTIAL) );
		exchange.getRequestHeaders().put(Headers.AUTHORIZATION, AUTHORIZATION);
		final Account authenticated = mechanism.authenticate(exchange, Arrays.asList(identityManager), session);
		assertNotNull(authenticated);
	}

	@Test
	public void ensureThatDoNotSendCredentialsToIdentityManagerWhenNoHeaderIsPresent(){
		doReturn(account).when(identityManager).verify( any() );
		final Account authenticated = mechanism.authenticate(exchange, Arrays.asList(identityManager), session);
		assertNull(authenticated);
		verify( identityManager, never() ).verify( any() );
	}

	@Test
	public void ensureThatDoNotSendCredentialsToIdentityManagerWhenInvalidHeaderIsPresent(){
		doReturn(account).when(identityManager).verify( any() );
		exchange.getRequestHeaders().put(Headers.AUTHORIZATION, AUTHORIZATION.toUpperCase());
		final Account authenticated = mechanism.authenticate(exchange, Arrays.asList(identityManager), session);
		assertNull(authenticated);
	}

	@Test
	public void ensureThatIsAbleToSentCorrectChallengeData(){
		mechanism.sendAuthenticationChallenge(exchange, session);
		assertEquals(exchange.getStatusCode(), 401);
		final String header = exchange.getResponseHeaders().get( Headers.WWW_AUTHENTICATE ).getFirst();
		assertEquals(header, "Basic realm=\"default\"");
	}
}
