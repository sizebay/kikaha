package kikaha.core.modules.security;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationHttpHandler implements HttpHandler {

	final AuthenticationRuleMatcher authenticationRuleMatcher;
	final String permissionDeniedPage;
	final HttpHandler next;
	final SecurityContextFactory securityContextFactory;

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		AuthenticationRule rule = retrieveRuleThatEnsureRequestShouldBeAuthenticated( exchange );
		if ( rule == null )
			next.handleRequest(exchange);
		else
			runAuthenticationInIOThread( exchange, rule );
	}

	AuthenticationRule retrieveRuleThatEnsureRequestShouldBeAuthenticated( final HttpServerExchange exchange ) {
		String relativePath = exchange.getRelativePath();
		return authenticationRuleMatcher.retrieveAuthenticationRuleForUrl( relativePath );
	}

	void runAuthenticationInIOThread(
			final HttpServerExchange exchange, final AuthenticationRule rule )
	{
		SecurityContext context = createSecurityContext( exchange, rule );
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
				exchange, next, context, rule.expectedRoles(), permissionDeniedPage ) );
	}
}