package kikaha.core.auth;

import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.impl.SecurityContextFactoryImpl;
import io.undertow.server.HttpServerExchange;
import trip.spi.Singleton;

@Singleton
public class DefaultSecurityContextFactory implements SecurityContextFactory {

	final io.undertow.security.api.SecurityContextFactory contextFactory = SecurityContextFactoryImpl.INSTANCE;

	@Override
	public SecurityContext createSecurityContextFor( HttpServerExchange exchange, AuthenticationRule rule ) {
		return contextFactory.createSecurityContext(
				exchange, AuthenticationMode.PRO_ACTIVE,
				rule.identityManager(), null );
	}
}
