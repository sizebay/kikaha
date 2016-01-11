package kikaha.core.security;

import io.undertow.server.HttpServerExchange;
import trip.spi.Singleton;

@Singleton
public class RESTAPISecurityContextFactory implements SecurityContextFactory {

	private final SessionStore emptySessionStore = new EmptySessionStore();

	@Override
	public SecurityContext createSecurityContextFor( HttpServerExchange exchange, AuthenticationRule rule ) {
		return new DefaultSecurityContext( rule, exchange, emptySessionStore );
	}
}
