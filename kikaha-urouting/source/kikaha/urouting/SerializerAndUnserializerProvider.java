package kikaha.urouting;

import java.io.IOException;
import java.util.Map;

import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Serializer;
import kikaha.urouting.api.Unserializer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SerializerAndUnserializerProvider {

	final Map<String, Serializer> serializerByContentType;
	final Map<String, Unserializer> unserializerByContentType;

	public Serializer getSerializerFor( final String contentType, final String fallbackContentType ) throws IOException{
		Serializer serializer = serializerByContentType.get(contentType);
		if ( serializer == null && fallbackContentType != null )
			serializer = serializerByContentType.get(fallbackContentType);
		if ( serializer == null )
			throw new RoutingException( "No serializer found for " + contentType );
		return serializer;
	}

	/**
	 * Retrieves an {@link Unserializer} for a given {@code contentType}
	 * argument. When no {@link Unserializer} is found it uses the
	 * {@code defaulConsumingContentType} argument to seek another one. It
	 * throws {@link RoutingException} when no decoder was found.
	 *
	 * @param contentType
	 * @param fallbackContentType
	 * @return
	 * @throws IOException
	 */
	public Unserializer getUnserializerFor( final String contentType, final String fallbackContentType ) throws IOException{
		Unserializer serializer = unserializerByContentType.get(contentType);
		if ( serializer == null && fallbackContentType != null )
			serializer = unserializerByContentType.get(fallbackContentType);
		if ( serializer == null )
			throw new RoutingException( "No unserializer found for " + contentType );
		return serializer;
	}
}
