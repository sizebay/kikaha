package kikaha.urouting.api;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;

public interface Serializer {

	<T> void serialize( final T object, final HttpServerExchange output ) throws IOException;
}