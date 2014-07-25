package io.skullabs.undertow.standalone.auth;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.skullabs.undertow.standalone.HttpServerExchangeStub;
import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;

import java.util.ArrayList;
import java.util.List;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuthenticatedHttpHandlerTest {

	@Mock
	IdentityManager identityManager;

	@Mock
	AuthenticationMechanism authMechanism;

	@Mock
	NotificationReceiver authenticationRequiredHandler;

	@Mock
	SecurityContext securityContext;

	@Mock
	RequestHookChain requestChain;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
	}

	@Test
	public void ensureThatAreAbleToRegisterAllAuthenticationMechanismsToSecurityContext() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		authHandler.execute( requestChain, exchange );
		verify( securityContext ).addAuthenticationMechanism( authMechanism );
	}

	@Test
	public void ensureThatSetAuthenticationAsRequired() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		authHandler.execute( requestChain, exchange );
		verify( securityContext ).setAuthenticationRequired();
	}

	@Test
	public void ensureThatIsListenForAuthenticationEvents() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		authHandler.execute( requestChain, exchange );
		verify( securityContext ).registerNotificationReceiver( authenticationRequiredHandler );
	}

	@Test
	public void ensureThatCouldCallTheTargetHttpHandlerWhenIsAuthenticated() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		when( securityContext.authenticate() ).thenReturn( true );
		authHandler.execute( requestChain, exchange );
		verify( requestChain ).executeNext();
	}

	@Test
	public void ensureThatNotCallTheTargetHttpHandleWhenWasNotAuthenticatedRequests() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		when( securityContext.authenticate() ).thenReturn( false );
		authHandler.execute( requestChain, exchange );
		verify( requestChain, never() ).executeNext();
	}

	AuthenticationHook createMockedAuthenticatedHandlerFor( final HttpServerExchange exchange ) {
		val authMechs = createListOfAuthenticationMechanisms();
		val authHandler = spy( new AuthenticationHook( identityManager, authMechs, authenticationRequiredHandler ) );
		when( authHandler.createSecurityContext( exchange ) ).thenReturn( securityContext );
		return authHandler;
	}

	List<AuthenticationMechanism> createListOfAuthenticationMechanisms() {
		val authMechs = new ArrayList<AuthenticationMechanism>();
		authMechs.add( authMechanism );
		return authMechs;
	}
}
