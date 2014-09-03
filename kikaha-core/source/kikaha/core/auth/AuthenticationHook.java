package kikaha.core.auth;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import kikaha.core.api.RequestHook;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.UndertowStandaloneException;
import kikaha.core.api.conf.Configuration;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class AuthenticationHook implements RequestHook {

	final AuthenticationRuleMatcher authenticationRuleMatcher;
	final Configuration configuration;

	@Override
	public void execute( final RequestHookChain chain, final HttpServerExchange exchange ) throws UndertowStandaloneException {
		val rule = retrieveRuleThatEnsureRequestShouldBeAuthenticated( exchange );
		if ( rule == null )
			chain.executeNext();
		else
			runAuthenticationInIOThread( chain, exchange, rule );
	}

	void runAuthenticationInIOThread( final RequestHookChain chain, final HttpServerExchange exchange,
			final kikaha.core.auth.AuthenticationRule rule )
			throws UndertowStandaloneException {
		val context = createSecurityContext( exchange, rule );
		chain.executeInWorkerThread(
			new AuthenticationRunner(
				context, chain, rule.expectedRoles(),
				configuration.authentication().formAuth() ) );
	}

	SecurityContext createSecurityContext( final HttpServerExchange exchange, final AuthenticationRule rule ) {
		return rule.securityContextFactory().createSecurityContextFor( exchange, rule );
	}

	AuthenticationRule retrieveRuleThatEnsureRequestShouldBeAuthenticated( final HttpServerExchange exchange ) {
		val relativePath = retrieveRelativePath( exchange );
		return authenticationRuleMatcher.retrieveAuthenticationRuleForUrl( relativePath );
	}

	String retrieveRelativePath( final HttpServerExchange exchange ) {
		return exchange.getRelativePath();
	}
}