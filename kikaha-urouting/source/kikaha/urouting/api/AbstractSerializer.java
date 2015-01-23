package kikaha.urouting.api;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.io.OutputStream;

import kikaha.urouting.UncloseableWriterWrapper;

public abstract class AbstractSerializer implements Serializer {

	@Override
	public <T> void serialize( final T object, final HttpServerExchange exchange ) throws IOException {
		if ( !exchange.isBlocking() )
			exchange.startBlocking();
		final OutputStream outputStream = exchange.getOutputStream();
		serialize( object, UncloseableWriterWrapper.wrap( outputStream ) );
		outputStream.flush();
	}

	abstract public <T> void serialize( final T object, final OutputStream output ) throws IOException;
}
