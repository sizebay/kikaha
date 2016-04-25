package kikaha.hazelcast;

import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.security.AuthenticationRule;
import kikaha.core.modules.security.DefaultSecurityContext;
import kikaha.core.modules.security.SecurityContext;
import kikaha.core.modules.security.SecurityContextFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HazelcastSecurityContextFactory implements SecurityContextFactory {

	@Inject
	HazelcastSessionStore store;

	@Override
	public SecurityContext createSecurityContextFor(HttpServerExchange exchange, AuthenticationRule rule ) {
		return new DefaultSecurityContext( rule, exchange, store );
	}
}
