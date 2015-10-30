package kikaha.urouting.producers;

import trip.spi.Singleton;
import io.undertow.server.HttpServerExchange;
import kikaha.core.security.SecurityContext;
import kikaha.urouting.api.ContextProducer;
import kikaha.urouting.api.RoutingException;

@Singleton( exposedAs = ContextProducer.class )
public class SecurityContextContextProducer implements ContextProducer<SecurityContext> {

	@Override
	public SecurityContext produce( HttpServerExchange exchange ) throws RoutingException {
		return (SecurityContext)exchange.getSecurityContext();
	}
}
