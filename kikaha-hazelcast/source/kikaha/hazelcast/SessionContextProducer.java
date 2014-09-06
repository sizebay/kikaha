package kikaha.hazelcast;

import io.undertow.server.HttpServerExchange;
import kikaha.urouting.api.ContextProducer;
import kikaha.urouting.api.RoutingException;
import lombok.val;
import trip.spi.Singleton;

@Singleton( exposedAs = ContextProducer.class )
public class SessionContextProducer implements ContextProducer<AuthenticatedSession> {

	@Override
	public AuthenticatedSession produce( HttpServerExchange exchange ) throws RoutingException {
		val securityContext = exchange.getSecurityContext();
		if ( securityContext == null )
			return AuthenticatedSession.from( exchange );
		return AuthenticatedSession.from( null, exchange, securityContext.getAuthenticatedAccount() );
	}
}
