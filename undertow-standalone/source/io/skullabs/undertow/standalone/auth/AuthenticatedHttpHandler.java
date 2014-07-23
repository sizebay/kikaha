package io.skullabs.undertow.standalone.auth;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityContextFactory;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.SecurityContextFactoryImpl;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class AuthenticatedHttpHandler implements HttpHandler {

	final SecurityContextHandler securityContextHandler = SecurityContextHandler.DEFAULT;
	final SecurityContextFactory contextFactory = SecurityContextFactoryImpl.INSTANCE;
	final IdentityManager identityManager;
	final List<AuthenticationMechanism> authenticationMechanisms;
	final HttpHandler authenticatedRoute;
	final HttpHandler authenticationRequiredHandler;

	@Override
	public void handleRequest( HttpServerExchange exchange ) throws Exception {
		val context = createSecurityContext( exchange );
		populateWithAuthenticationMechanisms( context );
		context.setAuthenticationRequired();
		if ( !context.authenticate() )
			handleAuthenticationRequired( exchange );
		else
			authenticatedRoute.handleRequest( exchange );
	}

	SecurityContext createSecurityContext( final HttpServerExchange exchange ) {
		val newContext = this.contextFactory.createSecurityContext(
				exchange, AuthenticationMode.PRO_ACTIVE, identityManager, null );
		securityContextHandler.setSecurityContext( exchange, newContext );
		return newContext;
	}

	void populateWithAuthenticationMechanisms( final SecurityContext context ) {
		for ( val authenticationMechanism : authenticationMechanisms )
			context.addAuthenticationMechanism( authenticationMechanism );
	}

	void handleAuthenticationRequired( HttpServerExchange exchange ) throws Exception {
		if ( authenticationRequiredHandler != null )
			authenticationRequiredHandler.handleRequest( exchange );
		else
			exchange.endExchange();
	}
}
