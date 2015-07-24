package kikaha.urouting.serializers.jackson;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;

import kikaha.urouting.api.ContentType;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Unserializer;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

@ContentType(Mimes.JSON)
@Singleton( exposedAs = Unserializer.class )
public class JSONUnserializer implements Unserializer {

	@Provided
	Jackson jackson;

	@Override
	public <T> T unserialize(HttpServerExchange input, Class<T> targetClass, String encoding) throws IOException {
		final ObjectMapper mapper = jackson.objectMapper();
		return mapper.readValue(input.getInputStream(), targetClass);
	}
}
