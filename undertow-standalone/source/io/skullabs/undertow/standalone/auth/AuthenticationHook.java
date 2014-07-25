package io.skullabs.undertow.standalone.auth;

import io.skullabs.undertow.standalone.api.RequestHook;
import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityContextFactory;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.SecurityContextFactoryImpl;
import io.undertow.server.HttpServerExchange;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class AuthenticationHook implements RequestHook {

	final SecurityContextHandler securityContextHandler = SecurityContextHandler.DEFAULT;
	final SecurityContextFactory contextFactory = SecurityContextFactoryImpl.INSTANCE;
	final IdentityManager identityManager;
	final List<AuthenticationMechanism> authenticationMechanisms;
	final NotificationReceiver authenticationRequiredHandler;

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

	void registerNotificationReceivers( final SecurityContext context ) {
		if ( thereIsSomeoneListeningForAuthenticationEvents() )
			context.registerNotificationReceiver( authenticationRequiredHandler );
	}

	void handleNotAuthenticatedExchange( HttpServerExchange exchange ) {
		if ( !thereIsSomeoneListeningForAuthenticationEvents() )
			exchange.endExchange();
	}

	boolean thereIsSomeoneListeningForAuthenticationEvents() {
		return authenticationRequiredHandler != null;
	}

	@Override
	public void execute( RequestHookChain chain, HttpServerExchange exchange ) throws UndertowStandaloneException {
		val context = createSecurityContext( exchange );
		populateWithAuthenticationMechanisms( context );
		registerNotificationReceivers( context );
		context.setAuthenticationRequired();

		if ( !context.authenticate() )
			handleNotAuthenticatedExchange( exchange );
		else
			chain.executeNext();
	}
}
