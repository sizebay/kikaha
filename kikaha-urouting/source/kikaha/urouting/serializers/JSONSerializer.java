package kikaha.urouting.serializers;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.nio.ByteBuffer;

import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Serializer;
import lombok.val;
import trip.spi.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton( name = Mimes.JSON, exposedAs = Serializer.class )
public class JSONSerializer implements Serializer {
	
	final ObjectMapper mapper = Jackson.createMapper();

	@Override
	public <T> void serialize(T object, HttpServerExchange exchange) throws IOException {
		val buffer = ByteBuffer.wrap( mapper.writeValueAsBytes(object) );
		exchange.getResponseSender().send( buffer );
		exchange.dispatch();
	}
}
