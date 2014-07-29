package io.skullabs.undertow.standalone.auth;

import io.skullabs.undertow.standalone.api.RequestHook;
import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationHook implements RequestHook {

	final AuthenticationRuleMatcher authenticationRuleMatcher;

	@Override
	public void execute( RequestHookChain chain, HttpServerExchange exchange ) throws UndertowStandaloneException {
		final AuthenticationRule rule = retrieveRuleThatEnsureRequestShouldBeAuthenticated( exchange );
		if ( rule == null )
			chain.executeNext();
		else
			chain.executeInIOThread( new AuthenticationRunner( rule, chain ) );
	}

	AuthenticationRule retrieveRuleThatEnsureRequestShouldBeAuthenticated( HttpServerExchange exchange ) {
		final String relativePath = retrieveRelativePath( exchange );
		return authenticationRuleMatcher.retrieveAuthenticationRuleForUrl( relativePath );
	}

	String retrieveRelativePath( HttpServerExchange exchange ) {
		return exchange.getRelativePath();
	}
}