package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;

/**
 *
 */
public interface AuthenticationRequestMatcher {

	boolean matches( HttpServerExchange exchange );
}
