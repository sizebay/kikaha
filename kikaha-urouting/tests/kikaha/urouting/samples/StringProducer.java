package kikaha.urouting.samples;

import kikaha.urouting.api.ContextProducer;
import io.undertow.server.HttpServerExchange;
import trip.spi.Singleton;

@Singleton( exposedAs = ContextProducer.class )
public class StringProducer implements ContextProducer<String> {

	public static final String HELLO_WORLD = "Hello World";

	@Override
	public String produce( HttpServerExchange exchange ) {
		return HELLO_WORLD;
	}
}
