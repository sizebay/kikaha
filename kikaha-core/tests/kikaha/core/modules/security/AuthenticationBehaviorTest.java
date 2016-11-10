package kikaha.core.modules.security;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import io.undertow.security.idm.*;
import io.undertow.server.HttpServerExchange;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationBehaviorTest {

	static final String USERNAME = "username";
	static final String PASSWORD = "password";
	static final Credential CREDENTIAL = new UsernameAndPasswordCredential(USERNAME, PASSWORD);

	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
	final Session session = new DefaultSession("1");

	@Mock AuthenticationMechanism mechanism;
	@Mock IdentityManager identityManager;
	@Mock AuthenticationRule rule;
	@Mock SessionStore store;
	@Mock SessionIdManager manager;
	@Mock Account account;

	@Before
	public void configureRule(){
		doAnswer(new SendDefaultUsernameAndPasswordAsCredential())
			.when(mechanism).authenticate( any(), any(), any() );
		doReturn( Arrays.asList(mechanism)).when( rule ).mechanisms();
		doReturn( Arrays.asList(identityManager)).when( rule ).identityManagers();
	}

	@Before
	public void configureSession(){
		doReturn(session).when(store).createOrRetrieveSession(any(), any());
	}

	@Test
	public void ensureThatDoesNotSendChallengeWhenUserIsAuthenticated(){
		doReturn(account).when(mechanism).authenticate(any(), any(), any());
		final SecurityContext context = new DefaultSecurityContext(rule, exchange, store, manager);
		assertTrue( context.authenticate() );
		verify( mechanism, never() ).sendAuthenticationChallenge(any(), any());
	}

	@Test
	public void ensureThatSendChallengeWhenUserIsntAuthenticated(){
		doReturn(null).when(mechanism).authenticate(any(), any(), any());
		doReturn(true).when(mechanism).sendAuthenticationChallenge( any(),any() );
		final SecurityContext context = new DefaultSecurityContext(rule, exchange, store, manager);
		assertFalse( context.authenticate() );
		verify( mechanism ).sendAuthenticationChallenge(any(), any());
	}
}

@SuppressWarnings("unchecked")
class SendDefaultUsernameAndPasswordAsCredential implements Answer<Account> {

	@Override
	public Account answer(InvocationOnMock invocation) throws Throwable {
		final AuthenticationMechanism mechanism = (AuthenticationMechanism)invocation.getMock();
		final Iterable<IdentityManager> managers = invocation.getArgumentAt(1, Iterable.class);
		return mechanism.verify(managers, AuthenticationBehaviorTest.CREDENTIAL);
	}
}