package kikaha.mustache;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;

import kikaha.urouting.api.ContentType;
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

	@Override
	public <T> void serialize( final T object, final HttpServerExchange exchange ) throws IOException {
		final MustacheTemplate template = (MustacheTemplate)object;
		String serialized = factory.serializer().serialize( template );
		exchange.getResponseSender().send( serialized );
	}
}