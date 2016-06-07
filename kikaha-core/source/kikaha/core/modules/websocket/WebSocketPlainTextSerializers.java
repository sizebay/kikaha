package kikaha.core.modules.websocket;

import javax.inject.Singleton;
import kikaha.core.modules.http.ContentType;

/**
 * A basic Serializer/Unserializer for websocket communications.
 */
@Singleton
@ContentType( "text/plain" )
public class WebSocketPlainTextSerializers
	implements WebSocketSession.Serializer, WebSocketSession.Unserializer {

	@Override
	public String serialize(Object object) {
		return object.toString();
	}

	@Override
	public <T> T unserialize(String data, Class<T> expectedClass) {
		if ( String.class.equals( expectedClass ) )
			return (T)data;
		throw new UnsupportedOperationException("unserialize not implemented yet!");
	}
}
