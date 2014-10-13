package kikaha.urouting.serializers;

import java.io.IOException;
import java.io.OutputStream;

import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Serializer;
import trip.spi.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton( name = Mimes.JSON, exposedAs = Serializer.class )
public class JSONSerializer implements Serializer {

	final ObjectMapper mapper = new ObjectMapper();

	@Override
	public <T> void serialize(final T object, final OutputStream output) throws RoutingException {
		try {
			mapper.writeValue(output, object);
		} catch (final IOException cause) {
			throw new RoutingException(cause);
		}
	}

}
