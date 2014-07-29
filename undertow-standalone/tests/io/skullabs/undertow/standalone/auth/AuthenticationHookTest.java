package io.skullabs.undertow.standalone.auth;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuthenticationHookTest {

	@Mock
	SecurityContext securityContext;

	@Mock
	RequestHookChain requestChain;
	AuthenticationRuleMatcher matcher;
	AuthenticationRule matchedRule;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		this.matcher = mockAuthRuleMatcher();
		this.matchedRule = this.matcher.retrieveAuthenticationRuleForUrl( "/user" );
	}

	AuthenticationRuleMatcher mockAuthRuleMatcher() {
		val defaultConfig = DefaultConfiguration.loadDefaultConfig().getConfig( "undertow.auth" );
		val authConfig = new DefaultAuthenticationConfiguration( defaultConfig );
		val authRuleMatcher = spy( new AuthenticationRuleMatcher( authConfig ) );
		return authRuleMatcher;
	}

	@Test
	public void ensureThatRunUnauthenticatedRoutesWhenAuthRuleMatcherDoesntMatchTheRequest() throws UndertowStandaloneException {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		doReturn( null ).when( matcher ).retrieveAuthenticationRuleForUrl( any( String.class ) );
		authHandler.execute( requestChain, exchange );
		verify( authHandler, never() )
				.executeRequestOnlyIfAuthenticate( eq( requestChain ), any( AuthenticationRule.class ), eq( exchange ) );
	}

	@Test
	public void ensureThatAreAbleToRegisterAllAuthenticationMechanismsToSecurityContext() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		doReturn( matchedRule ).when( matcher ).retrieveAuthenticationRuleForUrl( any( String.class ) );
		authHandler.execute( requestChain, exchange );
		verify( securityContext ).addAuthenticationMechanism( matchedRule.mechanisms().get( 0 ) );
	}

	@Test
	public void ensureThatSetAuthenticationAsRequired() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		doReturn( matchedRule ).when( matcher ).retrieveAuthenticationRuleForUrl( any( String.class ) );
		authHandler.execute( requestChain, exchange );
		verify( securityContext ).setAuthenticationRequired();
	}

	@Test
	public void ensureThatIsListenForAuthenticationEvents() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		val matchedRule = mock( AuthenticationRule.class );
		val notificationReceiver = mock( NotificationReceiver.class );
		doReturn( securityContext ).when( authHandler ).createSecurityContext( exchange, matchedRule );
		doReturn( matchedRule ).when( matcher ).retrieveAuthenticationRuleForUrl( any( String.class ) );
		doReturn( notificationReceiver ).when( matchedRule ).notificationReceiver();
		doReturn( true ).when( matchedRule ).isThereSomeoneListeningForAuthenticationEvents();
		authHandler.execute( requestChain, exchange );
		verify( securityContext ).registerNotificationReceiver( matchedRule.notificationReceiver() );
	}

	@Test
	public void ensureThatCouldCallTheTargetHttpHandlerWhenIsAuthenticated() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		when( securityContext.authenticate() ).thenReturn( true );
		doReturn( matchedRule ).when( matcher ).retrieveAuthenticationRuleForUrl( any( String.class ) );
		authHandler.execute( requestChain, exchange );
		verify( requestChain ).executeNext();
	}

	@Test
	public void ensureThatNotCallTheTargetHttpHandleWhenWasNotAuthenticatedRequests() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		doReturn( matchedRule ).when( matcher ).retrieveAuthenticationRuleForUrl( any( String.class ) );
		when( securityContext.authenticate() ).thenReturn( false );
		authHandler.execute( requestChain, exchange );
		verify( requestChain, never() ).executeNext();
	}

	AuthenticationHook createMockedAuthenticatedHandlerFor( final HttpServerExchange exchange ) {
		val authHandler = spy( new AuthenticationHook( matcher ) );
		when( authHandler.createSecurityContext( exchange, matchedRule ) ).thenReturn( securityContext );
		when( authHandler.retrieveRelativePath( exchange ) ).thenReturn( "a non null string" );
		return authHandler;
	}

	static String anyString() {
		any( String.class );
		return "";
	}
}
