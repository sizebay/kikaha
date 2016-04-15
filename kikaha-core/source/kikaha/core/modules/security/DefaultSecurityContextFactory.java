package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class DefaultSecurityContextFactory implements SecurityContextFactory {

	private static final String MSG_NOT_PRODUCTION_READY = "The DefaultSecurityContextFactory isn't designed for production proposes. Please consider using the HazelcastSecurityContextFactory.";
	private final SessionStore inMemorySessionStore = new InMemorySessionStore();

	@PostConstruct
	public void postConstruct() {
		log.warn( MSG_NOT_PRODUCTION_READY );
	}

	@Override
	public SecurityContext createSecurityContextFor( HttpServerExchange exchange, AuthenticationRule rule ) {
		return new DefaultSecurityContext( rule, exchange, inMemorySessionStore );
	}
}
