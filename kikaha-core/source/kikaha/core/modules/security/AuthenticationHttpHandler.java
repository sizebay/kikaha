package kikaha.core.modules.security;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.*;

@RequiredArgsConstructor
class AuthenticationHttpHandler implements HttpHandler {

	@NonNull final AuthenticationRuleMatcher authenticationRuleMatcher;
	@NonNull final String permissionDeniedPage;
	@NonNull final HttpHandler next;
	@NonNull final SecurityContextFactory securityContextFactory;
	@NonNull final SessionStore sessionStore;
	@NonNull final SessionIdManager sessionIdManager;

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		final AuthenticationRule rule = retrieveRuleThatEnsureRequestShouldBeAuthenticated( exchange );
		if ( rule == null || isAuthenticated( exchange ) )
			next.handleRequest(exchange);
		else
			runAuthenticationInIOThread( exchange, rule );
	}

	private boolean isAuthenticated( final HttpServerExchange exchange ) {
		final SecurityContext securityContext = (SecurityContext)exchange.getSecurityContext();
		return securityContext != null && securityContext.isAuthenticated();
	}

	private AuthenticationRule retrieveRuleThatEnsureRequestShouldBeAuthenticated( final HttpServerExchange exchange ) {
		final String relativePath = exchange.getRelativePath();
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
		final SecurityContext securityContext = securityContextFactory.createSecurityContextFor(exchange, rule, sessionStore, sessionIdManager);
		exchange.addExchangeCompleteListener( new SecurityContextAutoUpdater( securityContext ) );
		return securityContext;
	}

	void runAuthenticationInIOThread(final HttpServerExchange exchange,
			final AuthenticationRule rule, final SecurityContext context)
	{
		exchange.dispatch(
			new AuthenticationRunner(
				exchange, next, context, rule.expectedRoles(), permissionDeniedPage ) );
	}
}