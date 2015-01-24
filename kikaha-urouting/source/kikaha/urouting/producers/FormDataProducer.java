package kikaha.urouting.producers;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;

import java.io.IOException;

import kikaha.urouting.api.ContextProducer;
import kikaha.urouting.api.RoutingException;
import lombok.extern.java.Log;
import trip.spi.Singleton;

@Log
@Singleton( exposedAs = ContextProducer.class )
public class FormDataProducer implements ContextProducer<FormData> {

	final static String COULD_NOT_PRODUCE_FORM_DATA = "Could not produce a FormData for this request.";
	final FormParserFactory formParserFactory = FormParserFactory.builder().build();

	@Override
	public FormData produce( final HttpServerExchange exchange ) throws RoutingException {
		try {
			final FormDataParser parser = formParserFactory.createParser( exchange );
			return parser.parseBlocking();
		} catch ( NullPointerException | IOException cause ) {
			log.severe( cause.getMessage() );
			throw new RoutingException( COULD_NOT_PRODUCE_FORM_DATA );
		}
	}
}
