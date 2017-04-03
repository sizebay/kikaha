package kikaha.core.modules.security;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
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
		AuthenticationRule rule = retrieveRuleThatEnsureRequestShouldBeAuthenticated( exchange );
		if ( rule == null )
			rule = AuthenticationRule.EMPTY;
		final SecurityContext securityContext = getOrCreateSecurityContext(exchange, rule);
		if ( securityContext.isAuthenticated() )
			try {
				next.handleRequest(exchange);
			} catch ( Throwable cause ) {
				cause.printStackTrace();
			}
		else
			runAuthenticationInIOThread( exchange, rule, securityContext );
	}

	private AuthenticationRule retrieveRuleThatEnsureRequestShouldBeAuthenticated( final HttpServerExchange exchange ) {
		final String relativePath = exchange.getRelativePath();
		final String referer = exchange.getRequestHeaders().getFirst( Headers.REFERER );
		return authenticationRuleMatcher.retrieveAuthenticationRuleForUrl( relativePath, referer );
	}

	private SecurityContext getOrCreateSecurityContext( final HttpServerExchange exchange, final AuthenticationRule rule ) {
		SecurityContext securityContext = (SecurityContext)exchange.getSecurityContext();
		if ( securityContext == null ) {
			securityContext = securityContextFactory.createSecurityContextFor(exchange, rule, sessionStore, sessionIdManager);
			exchange.setSecurityContext( securityContext );
		}
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