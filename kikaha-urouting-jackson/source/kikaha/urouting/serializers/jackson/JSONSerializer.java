package kikaha.urouting.serializers.jackson;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.nio.ByteBuffer;

import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Serializer;
import lombok.val;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton( name = Mimes.JSON, exposedAs = Serializer.class )
public class JSONSerializer implements Serializer {
	
	@Provided
	Jackson jackson;

	@Override
	public <T> void serialize(T object, HttpServerExchange exchange) throws IOException {
		val buffer = ByteBuffer.wrap( jackson.objectMapper().writeValueAsBytes(object) );
		send(exchange, buffer);
		exchange.dispatch();
	}

	public void send(HttpServerExchange exchange, final ByteBuffer buffer) {
		exchange.getResponseSender().send( buffer );
	}
}
