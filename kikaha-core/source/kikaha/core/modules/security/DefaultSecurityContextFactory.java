package kikaha.core.modules.security;

import javax.inject.Singleton;
import io.undertow.server.HttpServerExchange;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class DefaultSecurityContextFactory implements SecurityContextFactory {

	@Override
	public DefaultSecurityContext createSecurityContextFor(
			final HttpServerExchange exchange,
			final AuthenticationRule rule,
			final SessionStore sessionStore,
			final SessionIdManager sessionIdManager)
	{
		return new DefaultSecurityContext(rule, exchange, sessionStore,
				sessionIdManager, rule != AuthenticationRule.EMPTY);
	}
}
