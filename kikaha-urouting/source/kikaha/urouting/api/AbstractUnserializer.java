package kikaha.urouting.api;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.io.Reader;
import java.nio.channels.Channels;

public abstract class AbstractUnserializer implements Unserializer {

	@Override
	public <T> T unserialize(HttpServerExchange input, Class<T> targetClass, String encoding) throws IOException {
		final Reader reader = Channels.newReader( input.getRequestChannel(), encoding );
		return unserialize(reader, targetClass);
	}

	public abstract <T> T unserialize( final Reader input, final Class<T> targetClass ) throws IOException;
}
