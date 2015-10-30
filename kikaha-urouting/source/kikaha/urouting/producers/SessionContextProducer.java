package kikaha.urouting.producers;

import trip.spi.Singleton;
import io.undertow.server.HttpServerExchange;
import kikaha.core.security.SecurityContext;
import kikaha.core.security.Session;
import kikaha.urouting.api.ContextProducer;
import kikaha.urouting.api.RoutingException;

@Singleton( exposedAs = ContextProducer.class )
public class SessionContextProducer implements ContextProducer<Session> {

	@Override
	public Session produce( HttpServerExchange exchange ) throws RoutingException {
		final SecurityContext context = (SecurityContext)exchange.getSecurityContext();
		return context.getCurrentSession();
	}
}
