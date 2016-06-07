package kikaha.mustache;

import com.github.mustachejava.MustacheNotFoundException;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;

import kikaha.core.NotFoundHandler;
import kikaha.core.modules.http.ContentType;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Serializer;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;

@ContentType( Mimes.HTML )
@Singleton
@Typed( Serializer.class )
public class HtmlMustacheSerializer implements Serializer {

	@Inject
	MustacheSerializerFactory factory;

	@Inject
	NotFoundHandler notFoundHandler;

	@Override
	public <T> void serialize(final T object, final HttpServerExchange exchange, String encoding) throws IOException {
		try {
			final MustacheTemplate template = (MustacheTemplate) object;
			String serialized = factory.serializer().serialize(template);
			exchange.getResponseSender().send(serialized);
		} catch ( MustacheNotFoundException cause ) {
			handleNotFound( exchange );
		}
	}

	private void handleNotFound( final HttpServerExchange exchange ) throws IOException {
		try {
			notFoundHandler.handleRequest( exchange );
		} catch (Exception e) {
			throw new IOException( e );
		}
	}
}