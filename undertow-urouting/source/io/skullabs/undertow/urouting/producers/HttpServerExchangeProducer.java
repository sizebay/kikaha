package io.skullabs.undertow.urouting.producers;

import io.skullabs.undertow.urouting.api.ContextProducer;
import io.undertow.server.HttpServerExchange;
import trip.spi.Singleton;

@Singleton( exposedAs = ContextProducer.class )
public class HttpServerExchangeProducer implements ContextProducer<HttpServerExchange> {

	@Override
	public HttpServerExchange produce( HttpServerExchange exchange ) {
		return exchange;
	}
}
