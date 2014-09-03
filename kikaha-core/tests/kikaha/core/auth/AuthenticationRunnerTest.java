package kikaha.core.auth;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;

import java.util.HashSet;
import java.util.Set;

import kikaha.core.HttpServerExchangeStub;
import kikaha.core.api.RequestHookChain;
import kikaha.core.impl.conf.DefaultAuthenticationConfiguration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuthenticationRunnerTest {

	@Mock
	SecurityContext securityContext;

	@Mock
	RequestHookChain requestChain;

	AuthenticationRunner authHandler;
	HttpServerExchange exchange;
	AuthenticationRule matchedRule;

	@Test
	public void ensureThatSetAuthenticationAsRequired() throws Exception {
		doReturn( true ).when( securityContext ).authenticate();
		when( securityContext.getAuthenticatedAccount() ).thenReturn( new FixedUsernameAndRolesAccount( createExpectedRoles(), null ) );
		authHandler.run();
		verify( securityContext ).setAuthenticationRequired();
	}

	@Test
	public void ensureThatCouldCallTheTargetHttpHandlerWhenIsAuthenticated() throws Exception {
		when( securityContext.authenticate() ).thenReturn( true );
		when( securityContext.isAuthenticated() ).thenReturn( true );
		when( securityContext.getAuthenticatedAccount() ).thenReturn( new FixedUsernameAndRolesAccount( createExpectedRoles(), null ) );
		authHandler.run();
		verify( requestChain ).executeNext();
	}

	@Test
	public void ensureThatNotCallTheTargetHttpHandlerWhenDoesntMatchExpectedRoles() throws Exception {
		doNothing().when( authHandler ).endCommunicationWithClient();
		doNothing().when( authHandler ).sendForbidenError( any( HttpServerExchange.class ) );
		when( securityContext.authenticate() ).thenReturn( true );
		when( securityContext.isAuthenticated() ).thenReturn( true );
		val accountWithUnexpectedRoles = new FixedUsernameAndRolesAccount( new HashSet<String>(), null );
		when( securityContext.getAuthenticatedAccount() ).thenReturn( accountWithUnexpectedRoles );
		authHandler.run();
		verify( requestChain, never() ).executeNext();
		verify( authHandler ).handlePermitionDenied();
		verify( authHandler ).sendForbidenError( any( HttpServerExchange.class ) );
	}

	@Test
	public void ensureThatNotCallTheTargetHttpHandleWhenWasNotAuthenticatedRequests() throws Exception {
		doNothing().when( authHandler ).endCommunicationWithClient();
		when( securityContext.authenticate() ).thenReturn( false );
		authHandler.run();
		verify( requestChain, never() ).executeNext();
		verify( authHandler ).endCommunicationWithClient();
	}

	@Test
	public void ensureThatIsAbleToHandleExceptionsInRunMethod() throws Exception {
		doThrow( new IllegalStateException() ).when( securityContext ).authenticate();
		doNothing().when( authHandler ).handleException( any( Throwable.class ) );
		authHandler.run();
		verify( requestChain, never() ).executeNext();
		verify( authHandler ).handleException( isA( IllegalStateException.class ) );
	}

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		initializeExchange();
		initializeAuthHandler();
	}

	void initializeExchange() {
		this.exchange = HttpServerExchangeStub.createHttpExchange();
		doReturn( exchange ).when( requestChain ).exchange();
	}

	void initializeAuthHandler() {
		val formAuthConfig = DefaultConfiguration
			.loadDefaultConfiguration().authentication().formAuth();
		val matcher = mockAuthRuleMatcher();
		this.matchedRule = spy( matcher.retrieveAuthenticationRuleForUrl( "/user" ) );
		this.authHandler = spy( new AuthenticationRunner(
			securityContext, requestChain, createExpectedRoles(), formAuthConfig ) );
	}

	AuthenticationRuleMatcher mockAuthRuleMatcher() {
		val defaultConfig = DefaultConfiguration.loadDefaultConfig().getConfig( "server.auth" );
		val authConfig = new DefaultAuthenticationConfiguration( defaultConfig );
		val authRuleMatcher = spy( new AuthenticationRuleMatcher( authConfig ) );
		return authRuleMatcher;
	}

	Set<String> createExpectedRoles() {
		val roles = new HashSet<String>();
		roles.add( "Expected" );
		return roles;
	}
}
