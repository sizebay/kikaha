package kikaha.urouting.serializers.jackson;

import java.io.IOException;
import javax.inject.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import kikaha.core.modules.http.ContentType;
import kikaha.core.modules.websocket.WebSocketSession;
import kikaha.urouting.api.Mimes;

/**
 * WebSocket JSON serializer and unserializer.
 */
@Singleton
@ContentType(Mimes.JSON)
public class JSONWebSocketSerializer implements WebSocketSession.Serializer, WebSocketSession.Unserializer {

	@Inject Jackson jackson;

	@Override
	public String serialize(Object object) throws JsonProcessingException {
		return jackson.objectMapper().writeValueAsString( object );
	}

	@Override
	public <T> T unserialize(String data, Class<T> expectedClass) throws IOException {
		return jackson.objectMapper().readValue( data, expectedClass );
	}
}
