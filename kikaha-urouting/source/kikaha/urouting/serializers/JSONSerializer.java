package kikaha.urouting.serializers;

import java.io.IOException;
import java.io.Writer;

import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Serializer;
import trip.spi.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton( name = Mimes.JSON )
public class JSONSerializer implements Serializer {
	
	final ObjectMapper mapper = new ObjectMapper();

	@Override
	public <T> void serialize(T object, Writer output) throws RoutingException {
		try {
			mapper.writeValue(output, object);
		} catch (IOException cause) {
			throw new RoutingException(cause);
		}
	}

}
