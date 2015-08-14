package kikaha.urouting.serializers.jackson;

import kikaha.urouting.api.DefaultResponse;
import kikaha.urouting.api.ExceptionHandler;
import kikaha.urouting.api.Response;
import trip.spi.Singleton;

import com.fasterxml.jackson.core.JsonProcessingException;

@Singleton(exposedAs=ExceptionHandler.class)
public class JsonParseExceptionHandler
	implements ExceptionHandler<JsonProcessingException> {

	@Override
	public Response handle(final JsonProcessingException cause) {
		return DefaultResponse
					.ok("BAD REQUEST: " + cause.getMessage())
					.statusCode(400);
	}
}
