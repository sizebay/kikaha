package kikaha.urouting.api;

import java.io.*;
import io.undertow.server.HttpServerExchange;
import kikaha.urouting.UncloseableWriterWrapper;

/**
 * Blocking serializer abstraction.
 */
public abstract class AbstractSerializer implements Serializer {

	@Override
	public <T> void serialize(final T object, final HttpServerExchange exchange, String encoding) throws IOException {
		if ( !exchange.isBlocking() )
			exchange.startBlocking();
		final OutputStream outputStream = exchange.getOutputStream();
		serialize( object, UncloseableWriterWrapper.wrap( outputStream ) );
		outputStream.flush();
		exchange.endExchange();
	}

	abstract public <T> void serialize( final T object, final OutputStream output ) throws IOException;
}
