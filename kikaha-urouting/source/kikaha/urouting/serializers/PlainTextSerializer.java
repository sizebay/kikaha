package kikaha.urouting.serializers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Serializer;
import trip.spi.Singleton;

@Singleton( name = Mimes.PLAIN_TEXT, exposedAs = Serializer.class )
public class PlainTextSerializer implements Serializer {

	static final String NULL = "null";

	@Override
	public <T> void serialize( final T object, final OutputStream output ) throws RoutingException {
		try {
			final String serialized = object != null ? object.toString() : NULL;
			final OutputStreamWriter writer = new OutputStreamWriter( output );
			writer.write( serialized );
			writer.close();
		} catch ( final IOException cause ) {
			throw new RoutingException( cause );
		}
	}
}
