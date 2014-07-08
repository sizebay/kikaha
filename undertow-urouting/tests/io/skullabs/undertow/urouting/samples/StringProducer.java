package io.skullabs.undertow.urouting.samples;

import io.skullabs.undertow.urouting.api.ContextProducer;
import io.undertow.server.HttpServerExchange;
import trip.spi.Service;

@Service
public class StringProducer implements ContextProducer<String> {

	public static final String HELLO_WORLD = "Hello World";

	@Override
	public String produce( HttpServerExchange exchange ) {
		return HELLO_WORLD;
	}
}
