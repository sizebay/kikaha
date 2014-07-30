package io.skullabs.undertow.urouting.producers;

import io.skullabs.undertow.urouting.api.ContextProducer;
import io.skullabs.undertow.urouting.api.RoutingException;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;

import java.io.IOException;

import lombok.extern.java.Log;
import trip.spi.Singleton;

@Log
@Singleton( exposedAs = ContextProducer.class )
public class FormDataProducer implements ContextProducer<FormData> {

	final static String COULD_NOT_PRODUCE_FORM_DATA = "Could not produce a FormData for this request.";
	final FormParserFactory formParserFactory = FormParserFactory.builder().build();

	@Override
	public FormData produce( HttpServerExchange exchange ) throws RoutingException {
		try {
			final FormDataParser parser = formParserFactory.createParser( exchange );
			parser.parseBlocking();
			return exchange.getAttachment( FormDataParser.FORM_DATA );
		} catch ( NullPointerException | IOException cause ) {
			log.severe( cause.getMessage() );
			throw new RoutingException( COULD_NOT_PRODUCE_FORM_DATA );
		}
	}
}
