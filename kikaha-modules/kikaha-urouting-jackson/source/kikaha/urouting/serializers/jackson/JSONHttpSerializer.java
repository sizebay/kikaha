package kikaha.urouting.serializers.jackson;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.inject.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.http.ContentType;
import kikaha.urouting.api.*;
import lombok.val;

@ContentType(Mimes.JSON)
@Singleton
public class JSONHttpSerializer implements Serializer, Unserializer {

	@Inject
	Jackson jackson;

	@Override
	public <T> void serialize(T object, HttpServerExchange exchange, String encoding) throws IOException {
		val buffer = ByteBuffer.wrap( jackson.objectMapper().writeValueAsBytes(object) );
		send(exchange, buffer);
		exchange.dispatch();
	}

	public void send(HttpServerExchange exchange, final ByteBuffer buffer) {
		exchange.getResponseSender().send( buffer );
	}

	@Override
	public <T> T unserialize(HttpServerExchange input, Class<T> targetClass, String encoding) throws IOException {
		final ObjectMapper mapper = jackson.objectMapper();
		return mapper.readValue(input.getInputStream(), targetClass);
	}
}
