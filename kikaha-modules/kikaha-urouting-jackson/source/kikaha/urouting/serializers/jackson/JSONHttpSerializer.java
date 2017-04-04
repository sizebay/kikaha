package kikaha.urouting.serializers.jackson;

import java.io.IOException;
import javax.inject.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.http.ContentType;
import kikaha.urouting.api.*;

@ContentType(Mimes.JSON)
@Singleton
public class JSONHttpSerializer implements Serializer, Unserializer {

	@Inject
	Jackson jackson;

	@Override
	public <T> void serialize(T object, HttpServerExchange exchange, String encoding) throws IOException {
		final String string = jackson.objectMapper().writeValueAsString( object );
		send(exchange, string);
	}

	public void send(final HttpServerExchange exchange, final String string) {
		exchange.getResponseSender().send( string );
	}

	@Override
	public <T> T unserialize( final HttpServerExchange exchange, final Class<T> targetClass, byte[] bodyData, final String encoding ) throws IOException {
		final ObjectMapper mapper = jackson.objectMapper();
		return mapper.readValue( bodyData, targetClass );
	}
}
