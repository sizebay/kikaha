package io.skullabs.undertow.standalone.auth;

import io.skullabs.undertow.standalone.api.RequestHook;
import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityContextFactory;
import io.undertow.security.impl.SecurityContextFactoryImpl;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class AuthenticationHook implements RequestHook {

	final SecurityContextHandler securityContextHandler = SecurityContextHandler.DEFAULT;
	final SecurityContextFactory contextFactory = SecurityContextFactoryImpl.INSTANCE;

	final AuthenticationRuleMatcher authenticationRuleMatcher;

	@Override
	public void execute( RequestHookChain chain, HttpServerExchange exchange ) throws UndertowStandaloneException {
		AuthenticationRule rule = retrieveRuleThatEnsureRequestShouldBeAuthenticated( exchange );
		if ( rule == null )
			chain.executeNext();
		else
			executeRequestOnlyIfAuthenticate( chain, rule, exchange );
	}

	AuthenticationRule retrieveRuleThatEnsureRequestShouldBeAuthenticated( HttpServerExchange exchange ) {
		final String relativePath = retrieveRelativePath( exchange );
		return authenticationRuleMatcher.retrieveAuthenticationRuleForUrl( relativePath );
	}

	String retrieveRelativePath( HttpServerExchange exchange ) {
		return exchange.getRelativePath();
	}

	void executeRequestOnlyIfAuthenticate( RequestHookChain chain, AuthenticationRule rule, HttpServerExchange exchange )
			throws UndertowStandaloneException {
		val context = createSecurityContext( exchange, rule );
		populateWithAuthenticationMechanisms( context, rule );
		registerNotificationReceivers( context, rule );
		context.setAuthenticationRequired();

		if ( !context.authenticate() )
			handleNotAuthenticatedExchange( exchange, rule );
		else
			chain.executeNext();
	}

	SecurityContext createSecurityContext(
			final HttpServerExchange exchange, final AuthenticationRule rule ) {
		val newContext = this.contextFactory.createSecurityContext(
				exchange, AuthenticationMode.PRO_ACTIVE,
				rule.identityManager(), null );
		securityContextHandler.setSecurityContext( exchange, newContext );
		return newContext;
	}

	void populateWithAuthenticationMechanisms(
			final SecurityContext context, final AuthenticationRule rule ) {
		for ( val authenticationMechanism : rule.mechanisms() )
			context.addAuthenticationMechanism( authenticationMechanism );
	}

	void registerNotificationReceivers(
			final SecurityContext context, final AuthenticationRule rule ) {
		if ( rule.isThereSomeoneListeningForAuthenticationEvents() )
			context.registerNotificationReceiver( rule.notificationReceiver() );
	}

	void handleNotAuthenticatedExchange(
			final HttpServerExchange exchange, final AuthenticationRule rule ) {
		if ( !rule.isThereSomeoneListeningForAuthenticationEvents() )
			exchange.endExchange();
	}
}
