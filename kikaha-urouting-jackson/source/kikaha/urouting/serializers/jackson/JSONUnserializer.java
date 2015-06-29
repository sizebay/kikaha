package kikaha.urouting.serializers.jackson;

import java.io.IOException;
import java.io.Reader;

import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Unserializer;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton( name = Mimes.JSON, exposedAs = Unserializer.class )
public class JSONUnserializer implements Unserializer {
	
	@Provided
	Jackson jackson;

	@Override
	public <T> T unserialize(final Reader input, final Class<T> targetClass) throws IOException {
		return jackson.objectMapper().readValue(input, targetClass);
	}
}
