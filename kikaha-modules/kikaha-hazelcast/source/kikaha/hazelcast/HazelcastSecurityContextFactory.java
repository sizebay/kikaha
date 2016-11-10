package kikaha.hazelcast;

import javax.inject.*;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.security.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@Deprecated
public class HazelcastSecurityContextFactory implements SecurityContextFactory {

	@Inject
	HazelcastSessionStore store;

	public HazelcastSecurityContextFactory(){
		log.info( "HazelcastSecurityContextFactory is deprecated and should be removed on the next major release. " +
				"Please, take a look at the http://kikaha.io for more information" );
	}

	@Override
	public SecurityContext createSecurityContextFor(
			final HttpServerExchange exchange,
			final AuthenticationRule rule,
			final SessionStore sessionStore,
			final SessionIdManager sessionIdManager)
	{
		return new DefaultSecurityContext( rule, exchange, store, sessionIdManager );
	}
}
