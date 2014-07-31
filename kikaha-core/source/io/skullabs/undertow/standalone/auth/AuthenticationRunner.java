package io.skullabs.undertow.standalone.auth;

import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityContextFactory;
import io.undertow.security.impl.SecurityContextFactoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class AuthenticationRunner implements Runnable {

	final SecurityContextHandler securityContextHandler = SecurityContextHandler.DEFAULT;
	final SecurityContextFactory contextFactory = SecurityContextFactoryImpl.INSTANCE;

	final AuthenticationRule rule;
	final RequestHookChain chain;

	@Override
	public void run() {
		val context = createSecurityContext();
		populateWithAuthenticationMechanisms( context );
		registerNotificationReceivers( context );
		context.setAuthenticationRequired();

		if ( !context.authenticate() )
			handleNotAuthenticatedExchange();
		else
			tryExecuteChain();
	}

	SecurityContext createSecurityContext() {
		val exchange = chain.exchange();
		val newContext = this.contextFactory.createSecurityContext(
				exchange, AuthenticationMode.PRO_ACTIVE,
				rule.identityManager(), null );
		securityContextHandler.setSecurityContext( exchange, newContext );
		return newContext;
	}

	void populateWithAuthenticationMechanisms(
			final SecurityContext context ) {
		for ( val authenticationMechanism : rule.mechanisms() )
			context.addAuthenticationMechanism( authenticationMechanism );
	}

	void registerNotificationReceivers(
			final SecurityContext context ) {
		if ( rule.isThereSomeoneListeningForAuthenticationEvents() )
			context.registerNotificationReceiver( rule.notificationReceiver() );
	}

	void handleNotAuthenticatedExchange() {
		if ( !rule.isThereSomeoneListeningForAuthenticationEvents() )
			chain.exchange().endExchange();
	}

	void tryExecuteChain() {
		try {
			chain.executeNext();
		} catch ( UndertowStandaloneException e ) {
			handleException( e );
		}
	}

	void handleException( UndertowStandaloneException e ) {
		e.printStackTrace();
		val exchange = chain.exchange();
		if ( !exchange.isResponseStarted() )
			exchange.setResponseCode( 500 );
		exchange.endExchange();
	}
}
