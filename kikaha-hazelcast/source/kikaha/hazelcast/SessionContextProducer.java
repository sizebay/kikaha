package kikaha.hazelcast;

import io.undertow.server.HttpServerExchange;
import kikaha.urouting.api.ContextProducer;
import kikaha.urouting.api.RoutingException;
import lombok.val;
import trip.spi.Singleton;

@Singleton( exposedAs = ContextProducer.class )
public class SessionContextProducer implements ContextProducer<Session> {

	@Override
	public Session produce( HttpServerExchange exchange ) throws RoutingException {
		val securityContext = exchange.getSecurityContext();
		if ( securityContext == null )
			return Session.from( exchange );
		return Session.from( exchange, securityContext.getAuthenticatedAccount() );
	}
}
