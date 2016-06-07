package kikaha.urouting.serializers.jackson;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;

import kikaha.core.modules.http.ContentType;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Unserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;

@ContentType(Mimes.JSON)
@Singleton
@Typed(Unserializer.class )
public class JSONUnserializer implements Unserializer {

	@Inject
	Jackson jackson;

	@Override
	public <T> T unserialize(HttpServerExchange input, Class<T> targetClass, String encoding) throws IOException {
		final ObjectMapper mapper = jackson.objectMapper();
		return mapper.readValue(input.getInputStream(), targetClass);
	}
}
