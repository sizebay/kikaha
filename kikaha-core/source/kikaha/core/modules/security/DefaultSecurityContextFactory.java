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
		final boolean authenticationRequired = rule != AuthenticationRule.EMPTY && rule.authenticationRequired();
		return new DefaultSecurityContext(rule, exchange, securityConfiguration, authenticationRequired);
	}
}
