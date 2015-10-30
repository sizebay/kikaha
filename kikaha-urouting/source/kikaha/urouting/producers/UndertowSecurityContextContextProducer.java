package kikaha.urouting.producers;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import kikaha.urouting.api.ContextProducer;
import kikaha.urouting.api.RoutingException;
import trip.spi.Singleton;

@Singleton( exposedAs = ContextProducer.class )
public class UndertowSecurityContextContextProducer implements ContextProducer<SecurityContext> {

	@Override
	public SecurityContext produce( HttpServerExchange exchange ) throws RoutingException {
		return exchange.getSecurityContext();
	}
}
