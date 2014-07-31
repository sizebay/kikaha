package kikaha.urouting.serializers;

import java.io.IOException;
import java.io.Writer;

import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Serializer;
import trip.spi.Singleton;

@Singleton( name = Mimes.PLAIN_TEXT )
public class PlainTextSerializer implements Serializer {

	static final String NULL = "null";

	@Override
	public <T> void serialize( T object, Writer output ) throws RoutingException {
		try {
			String serialized = object != null ? object.toString() : NULL;
			output.write( serialized );
		} catch ( IOException cause ) {
			throw new RoutingException( cause );
		}
	}
}
