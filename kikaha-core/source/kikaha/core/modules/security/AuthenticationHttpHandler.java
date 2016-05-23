package kikaha.core.modules.security;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class AuthenticationHttpHandler implements HttpHandler {

	final AuthenticationRuleMatcher authenticationRuleMatcher;
	final String permissionDeniedPage;
	final HttpHandler next;
	final SecurityContextFactory securityContextFactory;

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		AuthenticationRule rule = retrieveRuleThatEnsureRequestShouldBeAuthenticated( exchange );
		if ( rule == null || isAuthenticated( exchange ) )
			next.handleRequest(exchange);
		else
			runAuthenticationInIOThread( exchange, rule );
	}

	private boolean isAuthenticated( final HttpServerExchange exchange ) {
		final SecurityContext securityContext = exchange.getSecurityContext();
		return securityContext != null && securityContext.isAuthenticated();
	}

	private AuthenticationRule retrieveRuleThatEnsureRequestShouldBeAuthenticated( final HttpServerExchange exchange ) {
		String relativePath = exchange.getRelativePath();
		return authenticationRuleMatcher.retrieveAuthenticationRuleForUrl( relativePath );
	}

	void runAuthenticationInIOThread(
			final HttpServerExchange exchange, final AuthenticationRule rule )
	{
		final SecurityContext context = createSecurityContext( exchange, rule );
		exchange.setSecurityContext( context );
		runAuthenticationInIOThread(exchange, rule, context);
	}

	private SecurityContext createSecurityContext( final HttpServerExchange exchange, final AuthenticationRule rule ) {
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