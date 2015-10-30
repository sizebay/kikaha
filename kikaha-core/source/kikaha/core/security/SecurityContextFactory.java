package kikaha.core.security;

import io.undertow.server.HttpServerExchange;

/**
 * A factory of {@link SecurityContext} for authentication routines.
 * 
 * @author Miere Teixeira
 */
public interface SecurityContextFactory {

	/**
	 * Create a {@link SecurityContext} for a given {@link AuthenticationRule}
	 * and {@link HttpServerExchange} arguments.<br>
	 * <br>
	 * Once is identified a request requires authentication it's mandatory to
	 * create a {@link SecurityContext} which will provide data for any
	 * authenticated route.
	 * 
	 * @param exchange
	 * @return
	 */
	SecurityContext createSecurityContextFor( HttpServerExchange exchange, AuthenticationRule rule );
}
