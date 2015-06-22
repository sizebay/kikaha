package kikaha.urouting.serializers.jackson;

import java.io.IOException;
import java.io.Reader;

import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Unserializer;
import trip.spi.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton( name = Mimes.JSON, exposedAs = Unserializer.class )
public class JSONUnserializer implements Unserializer {

	final ObjectMapper mapper = Jackson.createMapper();

	@Override
	public <T> T unserialize(final Reader input, final Class<T> targetClass) throws IOException {
		return mapper.readValue(input, targetClass);
	}
}
