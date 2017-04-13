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
			final SecurityConfiguration securityConfiguration )
	{
		return new DefaultSecurityContext(rule, exchange, securityConfiguration, rule != AuthenticationRule.EMPTY);
	}
}
