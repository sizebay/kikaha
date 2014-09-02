package kikaha.core.auth;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;

import java.util.HashSet;
import java.util.Set;

import kikaha.core.DefaultAuthenticationConfiguration;
import kikaha.core.DefaultConfiguration;
import kikaha.core.HttpServerExchangeStub;
import kikaha.core.api.RequestHookChain;
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
		when( securityContext.getAuthenticatedAccount() ).thenReturn( new FixedUsernameAndRolesAccount( createExpectedRoles(), null ) );
		authHandler.run();
		verify( requestChain ).executeNext();
	}

	@Test
	public void ensureThatNotCallTheTargetHttpHandlerWhenDoesntMatchExpectedRoles() throws Exception {
		when( securityContext.authenticate() ).thenReturn( true );
		val accountWithUnexpectedRoles = new FixedUsernameAndRolesAccount( new HashSet<String>(), null );
		when( securityContext.getAuthenticatedAccount() ).thenReturn( accountWithUnexpectedRoles );
		authHandler.run();
		verify( requestChain, never() ).executeNext();
	}

	@Test
	public void ensureThatNotCallTheTargetHttpHandleWhenWasNotAuthenticatedRequests() throws Exception {
		when( securityContext.authenticate() ).thenReturn( false );
		authHandler.run();
		verify( requestChain, never() ).executeNext();
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
		val matcher = mockAuthRuleMatcher();
		this.matchedRule = spy( matcher.retrieveAuthenticationRuleForUrl( "/user" ) );
		this.authHandler = spy( new AuthenticationRunner( securityContext, requestChain, createExpectedRoles() ) );
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
