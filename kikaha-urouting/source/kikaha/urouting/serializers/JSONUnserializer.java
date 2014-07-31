package kikaha.urouting.serializers;

import java.io.IOException;
import java.io.Reader;

import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Unserializer;
import trip.spi.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton( name = Mimes.JSON )
public class JSONUnserializer implements Unserializer {
	
	final ObjectMapper mapper = new ObjectMapper();

	@Override
	public <T> T unserialize(Reader input, Class<T> targetClass) throws RoutingException {
		try {
			return mapper.readValue(input, targetClass);
		} catch ( IOException cause ) {
			throw new RoutingException(cause);
		}
	}
}
