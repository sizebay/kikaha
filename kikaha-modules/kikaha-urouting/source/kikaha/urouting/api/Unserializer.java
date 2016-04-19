package kikaha.urouting.api;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;

public interface Unserializer {

	<T> T unserialize( final HttpServerExchange input, final Class<T> targetClass, String encoding ) throws IOException;
}
