package kikaha.urouting.serializers.jackson;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.nio.ByteBuffer;

import kikaha.core.modules.http.ContentType;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Serializer;
import lombok.val;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;

@ContentType(Mimes.JSON)
@Singleton
@Typed(Serializer.class )
public class JSONSerializer implements Serializer {

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
}
