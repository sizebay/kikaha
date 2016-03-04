package kikaha.core.security;

import io.undertow.server.HttpServerExchange;
import trip.spi.Singleton;

@Singleton
public class RESTAPISecurityContextFactory implements SecurityContextFactory {

	@Override
	public DefaultSecurityContext createSecurityContextFor( HttpServerExchange exchange, AuthenticationRule rule ) {
		return new DefaultSecurityContext(
			rule, exchange,
			new EmptySessionStore()
		);
	}
}
