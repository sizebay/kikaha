package kikaha.core.security;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import kikaha.core.api.KikahaException;
import kikaha.core.api.conf.Configuration;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class AuthenticationHttpHandler implements HttpHandler {

	final AuthenticationRuleMatcher authenticationRuleMatcher;
	final Configuration configuration;
	final HttpHandler next;
	final SecurityContextFactory securityContextFactory;

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		val rule = retrieveRuleThatEnsureRequestShouldBeAuthenticated( exchange );
		if ( rule == null )
			next.handleRequest(exchange);
		else
			runAuthenticationInIOThread( exchange, rule );
	}

	AuthenticationRule retrieveRuleThatEnsureRequestShouldBeAuthenticated( final HttpServerExchange exchange ) {
		val relativePath = exchange.getRelativePath();
		return authenticationRuleMatcher.retrieveAuthenticationRuleForUrl( relativePath );
	}

	void runAuthenticationInIOThread(
			final HttpServerExchange exchange, final AuthenticationRule rule ) throws KikahaException
	{
		val context = createSecurityContext( exchange, rule );
		exchange.setSecurityContext( context );
		runAuthenticationInIOThread(exchange, rule, context);
	}

	SecurityContext createSecurityContext( final HttpServerExchange exchange, final AuthenticationRule rule ) {
		return securityContextFactory.createSecurityContextFor( exchange, rule );
	}

	void runAuthenticationInIOThread(final HttpServerExchange exchange,
			final AuthenticationRule rule, final SecurityContext context)
	{
		exchange.dispatch(
			new AuthenticationRunner(
				exchange, next, context, rule.expectedRoles(),
				configuration.authentication().formAuth() ) );
	}
}