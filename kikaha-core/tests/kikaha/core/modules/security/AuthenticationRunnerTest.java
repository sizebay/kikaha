package kikaha.core.modules.security;

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
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.HashSet;
import java.util.Set;

import kikaha.config.Config;
import kikaha.core.HttpServerExchangeStub;
import kikaha.core.cdi.ServiceProvider;
import kikaha.core.test.KikahaRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;

@RunWith( KikahaRunner.class )
public class AuthenticationRunnerTest {

	@Inject
	Config config;

	@Inject
	ServiceProvider provider;

	@Mock
	SecurityContext securityContext;

	@Mock
	HttpHandler rootHandler;

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
		verify( rootHandler ).handleRequest(exchange);
	}

	@Test
	public void ensureThatNotCallTheTargetHttpHandlerWhenDoesntMatchExpectedRoles() throws Exception {
		doNothing().when( authHandler ).endCommunicationWithClient();
		doNothing().when( authHandler ).sendForbiddenError();
		when( securityContext.authenticate() ).thenReturn( true );
		when( securityContext.isAuthenticated() ).thenReturn( true );
		FixedUsernameAndRolesAccount accountWithUnexpectedRoles = new FixedUsernameAndRolesAccount( new HashSet<>(), null );
		when( securityContext.getAuthenticatedAccount() ).thenReturn( accountWithUnexpectedRoles );
		authHandler.run();
		verify( rootHandler, never() ).handleRequest(exchange);
		verify( authHandler ).handlePermissionDenied();
		verify( authHandler ).sendForbiddenError();
	}

	@Test
	public void ensureThatNotCallTheTargetHttpHandleWhenWasNotAuthenticatedRequests() throws Exception {
		doNothing().when( authHandler ).endCommunicationWithClient();
		when( securityContext.authenticate() ).thenReturn( false );
		authHandler.run();
		verify( rootHandler, never() ).handleRequest(exchange);
		verify( authHandler ).endCommunicationWithClient();
	}

	@Test
	public void ensureThatIsAbleToHandleExceptionsInRunMethod() throws Exception {
		doThrow( new IllegalStateException() ).when( securityContext ).authenticate();
		doNothing().when( authHandler ).handleException( any( Throwable.class ) );
		authHandler.run();
		verify( rootHandler, never() ).handleRequest(exchange);
		verify( authHandler ).handleException( isA( IllegalStateException.class ) );
	}

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		initializeExchange();
		initializeAuthHandler();
	}

	void initializeExchange() {
		exchange = HttpServerExchangeStub.createHttpExchange();
	}

	void initializeAuthHandler() {
		AuthenticationRuleMatcher matcher = mockAuthRuleMatcher();
		matchedRule = spy( matcher.retrieveAuthenticationRuleForUrl( "/user" ) );
		authHandler = spy( new AuthenticationRunner( exchange, rootHandler,
			securityContext, createExpectedRoles(), "" ) );
	}

	AuthenticationRuleMatcher mockAuthRuleMatcher() {
		return spy( new AuthenticationRuleMatcher( provider, config.getConfig("server.auth") ) );
	}

	Set<String> createExpectedRoles() {
		Set<String> roles = new HashSet<>();
		roles.add( "Expected" );
		return roles;
	}
}
