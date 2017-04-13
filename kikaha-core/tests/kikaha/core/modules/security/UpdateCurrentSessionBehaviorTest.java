package kikaha.core.modules.security;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpdateCurrentSessionBehaviorTest {

	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
	final Session session = new DefaultSession("1");

	@Mock AuthenticationMechanism mechanism;
	@Mock AuthenticationRule rule;
	@Mock Account account;

	@Mock IdentityManager identityManager;
	@Mock SessionStore store;

	@Mock AuthenticationSuccessListener authenticationSuccessListener;
	AuthenticationFailureListener authenticationFailureListener = spy( new SendChallengeFailureListener() );

	@Spy @InjectMocks
	SecurityConfiguration securityConfiguration;

	@Before
	public void configureRule(){
		doAnswer(new SendDefaultUsernameAndPasswordAsCredential())
			.when(mechanism).authenticate( any(), any(), any() );
		doReturn( Arrays.asList(mechanism)).when( rule ).mechanisms();
		doReturn( Arrays.asList(identityManager)).when( rule ).identityManagers();

		securityConfiguration.setAuthenticationFailureListener( authenticationFailureListener );
	}

	@Before
	public void configureSession(){
		doReturn(session).when(store).createOrRetrieveSession(any(),any());
	}

	@Test
	public void ensureThatCanUpdateCurrentSessionDataWhenChangeAnyAttribute() {
		final SecurityContext context = getAuthenticatedSecurityContext();
		final Session currentSession = context.getCurrentSession();
		currentSession.setAttribute( "blah", "newValue" );
		context.updateCurrentSession();
		verify( store, times( 2 ) ).flush( eq( currentSession ) );
	}

	@Test
	public void ensureThatCanUpdateCurrentSessionDataWhenChangeCurrentLoggedInAccount() {
		final SecurityContext context = getAuthenticatedSecurityContext();
		final Session currentSession = context.getCurrentSession();
		currentSession.setAuthenticatedAccount( account );
		context.updateCurrentSession();
		verify( store, times( 2 ) ).flush( eq( currentSession ) );
	}

	@Test
	public void ensureThatNOTUpdateCurrentSessionDataWhenNothingWasChanged() {
		final SecurityContext context = getAuthenticatedSecurityContext();
		final Session currentSession = context.getCurrentSession();
		context.updateCurrentSession();
		verify( store, times( 1 ) ).flush( eq( currentSession ) );
	}

	private SecurityContext getAuthenticatedSecurityContext() {
		doReturn( account ).when( mechanism ).authenticate( any(), any(), any() );
		final SecurityContext context = new DefaultSecurityContext(rule, exchange, securityConfiguration, true);
		assertTrue( context.authenticate() );
		verify( mechanism, never() ).sendAuthenticationChallenge( any(), any() );
		verify( store, times( 1 ) ).flush( any() );
		return context;
	}
}
