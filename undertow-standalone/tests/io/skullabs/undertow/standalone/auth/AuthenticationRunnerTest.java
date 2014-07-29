package io.skullabs.undertow.standalone.auth;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.skullabs.undertow.standalone.DefaultAuthenticationConfiguration;
import io.skullabs.undertow.standalone.DefaultConfiguration;
import io.skullabs.undertow.standalone.HttpServerExchangeStub;
import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
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
	public void ensureThatAreAbleToRegisterAllAuthenticationMechanismsToSecurityContext() throws Exception {
		when( securityContext.authenticate() ).thenReturn( true );
		authHandler.run();
		verify( securityContext ).addAuthenticationMechanism( matchedRule.mechanisms().get( 0 ) );
	}

	@Test
	public void ensureThatSetAuthenticationAsRequired() throws Exception {
		doReturn( true ).when( securityContext ).authenticate();
		authHandler.run();
		verify( securityContext ).setAuthenticationRequired();
	}

	@Test
	public void ensureThatIsListenForAuthenticationEvents() throws Exception {
		val notificationReceiver = mock( NotificationReceiver.class );
		doReturn( securityContext ).when( authHandler ).createSecurityContext();
		doReturn( notificationReceiver ).when( matchedRule ).notificationReceiver();
		doReturn( true ).when( matchedRule ).isThereSomeoneListeningForAuthenticationEvents();
		authHandler.run();
		verify( securityContext ).registerNotificationReceiver( matchedRule.notificationReceiver() );
	}

	@Test
	public void ensureThatCouldCallTheTargetHttpHandlerWhenIsAuthenticated() throws Exception {
		when( securityContext.authenticate() ).thenReturn( true );
		authHandler.run();
		verify( requestChain ).executeNext();
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
		this.authHandler = spy( new AuthenticationRunner( matchedRule, requestChain ) );
		doReturn( securityContext ).when( authHandler ).createSecurityContext();
	}

	AuthenticationRuleMatcher mockAuthRuleMatcher() {
		val defaultConfig = DefaultConfiguration.loadDefaultConfig().getConfig( "undertow.auth" );
		val authConfig = new DefaultAuthenticationConfiguration( defaultConfig );
		val authRuleMatcher = spy( new AuthenticationRuleMatcher( authConfig ) );
		return authRuleMatcher;
	}
}
