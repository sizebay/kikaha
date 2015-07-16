package kikaha.mustache;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.io.OutputStream;

import kikaha.urouting.UncloseableWriterWrapper;
import kikaha.urouting.api.ContentType;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Serializer;
import trip.spi.Provided;
import trip.spi.Singleton;

@ContentType( Mimes.HTML )
@Singleton( exposedAs = Serializer.class )
public class HtmlMustacheSerializer implements Serializer {

	@Provided
	MustacheSerializerFactory factory;

	@Override
	public <T> void serialize( final T object, final HttpServerExchange exchange ) throws IOException {
		final OutputStream outputStream = exchange.getOutputStream();
		final MustacheTemplate template = (MustacheTemplate)object;
		final HtmlResponse response = new HtmlResponse( exchange, template.paramObject() );
		template.paramObject( response );
		factory.serializer().serialize( template, UncloseableWriterWrapper.wrap( outputStream ) );
		outputStream.flush();
	}
}