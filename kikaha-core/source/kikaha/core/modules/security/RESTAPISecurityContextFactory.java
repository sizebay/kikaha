package kikaha.core.modules.security;

import javax.inject.Singleton;
import io.undertow.server.HttpServerExchange;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@Deprecated
public class RESTAPISecurityContextFactory extends DefaultSecurityContextFactory {

	final SessionStore sessionStore = new RESTAPISessionStore();

	public RESTAPISecurityContextFactory(){
		log.warn( "RESTAPISecurityContextFactory is deprecated and should be removed on the next major release. " +
				"Please, take a look at the http://kikaha.io for more information" );
	}

	@Override
	public DefaultSecurityContext createSecurityContextFor(
			final HttpServerExchange exchange,
			final AuthenticationRule rule,
			final SessionStore sessionStore,
			final SessionIdManager sessionIdManager)
	{
		return super.createSecurityContextFor( exchange, rule, this.sessionStore, sessionIdManager );
	}
}
