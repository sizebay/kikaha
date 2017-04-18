package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;

/**
 *
 */
public class ByPassAuthenticationRequestMatcher implements AuthenticationRequestMatcher {

	@Override
	public boolean matches( final HttpServerExchange exchange ) {
		return true;
	}
}
