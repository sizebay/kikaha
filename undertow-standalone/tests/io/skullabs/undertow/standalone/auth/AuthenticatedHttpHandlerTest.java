package io.skullabs.undertow.standalone.auth;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.skullabs.undertow.standalone.HttpServerExchangeStub;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpHandler;
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
	HttpHandler httpHandler;

	@Mock
	HttpHandler authenticationRequiredHandler;

	@Mock
	SecurityContext securityContext;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
	}

	@Test
	public void ensureThatAreAbleToByPassAllAuthenticationMechanismsToSecurityContext() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		authHandler.handleRequest( exchange );
		verify( securityContext ).addAuthenticationMechanism( authMechanism );
	}

	@Test
	public void ensureThatSetAuthenticationAsRequired() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		authHandler.handleRequest( exchange );
		verify( securityContext ).setAuthenticationRequired();
	}

	@Test
	public void ensureThatCouldCallTheTargetHttpHandlerWhenIsAuthenticated() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		when( securityContext.authenticate() ).thenReturn( true );
		authHandler.handleRequest( exchange );
		verify( httpHandler ).handleRequest( exchange );
	}

	@Test
	public void ensureThatNotCallTheTargetHttpHandleButHandleUnauthenticatedRequests() throws Exception {
		val exchange = HttpServerExchangeStub.createHttpExchange();
		val authHandler = createMockedAuthenticatedHandlerFor( exchange );
		when( securityContext.authenticate() ).thenReturn( false );
		authHandler.handleRequest( exchange );
		verify( httpHandler, never() ).handleRequest( exchange );
		verify( authenticationRequiredHandler ).handleRequest( exchange );
	}

	AuthenticatedHttpHandler createMockedAuthenticatedHandlerFor( final HttpServerExchange exchange ) {
		val authMechs = createListOfAuthenticationMechanisms();
		val authHandler = spy( new AuthenticatedHttpHandler( identityManager, authMechs, httpHandler, authenticationRequiredHandler ) );
		when( authHandler.createSecurityContext( exchange ) ).thenReturn( securityContext );
		return authHandler;
	}

	List<AuthenticationMechanism> createListOfAuthenticationMechanisms() {
		val authMechs = new ArrayList<AuthenticationMechanism>();
		authMechs.add( authMechanism );
		return authMechs;
	}
}
