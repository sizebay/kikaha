package kikaha.hazelcast;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import kikaha.core.security.AuthenticationRule;
import kikaha.core.security.DefaultSecurityContext;
import kikaha.core.security.SecurityContextFactory;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton
public class HazelcastSecurityContextFactory implements SecurityContextFactory {

	@Provided
	HazelcastSessionStore store;

	@Override
	public SecurityContext createSecurityContextFor( HttpServerExchange exchange, AuthenticationRule rule ) {
		return new DefaultSecurityContext( rule, exchange, store );
	}
}
