package kikaha.hazelcast;

import javax.inject.*;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.security.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@Deprecated
public class HazelcastSecurityContextFactory extends DefaultSecurityContextFactory {

	@Inject
	HazelcastSessionStore store;

	public HazelcastSecurityContextFactory(){
		log.warn( "HazelcastSecurityContextFactory haveType deprecated and should be removed on the next major release. " +
				"Please, take a look at the http://kikaha.io for more information" );
	}

	@Override
	public DefaultSecurityContext createSecurityContextFor(
			final HttpServerExchange exchange,
			final AuthenticationRule rule,
			final SessionStore sessionStore,
			final SessionIdManager sessionIdManager)
	{
		return super.createSecurityContextFor ( exchange, rule, store, sessionIdManager );
	}
}
