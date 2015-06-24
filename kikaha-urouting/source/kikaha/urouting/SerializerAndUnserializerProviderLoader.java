package kikaha.urouting;

import java.util.HashMap;
import java.util.Map;

import kikaha.urouting.api.ContentType;
import kikaha.urouting.api.Serializer;
import kikaha.urouting.api.Unserializer;
import trip.spi.ProvidedServices;
import trip.spi.ServiceProvider;
import trip.spi.Singleton;
import trip.spi.StartupListener;

@Singleton( exposedAs=StartupListener.class )
public class SerializerAndUnserializerProviderLoader
	implements StartupListener {

	@ProvidedServices(exposedAs=Serializer.class)
	Iterable<Serializer> availableSerializers;

	@ProvidedServices(exposedAs=Unserializer.class)
	Iterable<Unserializer> availableUnserializers;

	@Override
	public void onStartup( final ServiceProvider provider ) {
		final Map<String, Serializer> serializers = loadAllSerializers();
		final Map<String, Unserializer> unserializers = loadAllUnserializers();
		final SerializerAndUnserializerProvider serializerAndUnserializerProvider
			= new SerializerAndUnserializerProvider(serializers, unserializers);
		provider.providerFor(SerializerAndUnserializerProvider.class, serializerAndUnserializerProvider);
	}

	private Map<String, Serializer> loadAllSerializers() {
		final Map<String,Serializer> serializers = new HashMap<>();
		for ( final Serializer serializer : availableSerializers )
			serializers.put( extractContentTypeFrom(serializer), serializer );
		return serializers;
	}

	private Map<String, Unserializer> loadAllUnserializers() {
		final Map<String,Unserializer> unserializers = new HashMap<>();
		for ( final Unserializer unserializer : availableUnserializers )
			unserializers.put( extractContentTypeFrom(unserializer), unserializer );
		return unserializers;
	}

	String extractContentTypeFrom( final Object target ){
		final Class<?> clazz = target.getClass();
		final Singleton annotation = clazz.getAnnotation(Singleton.class);
		if ( annotation!=null )
			return annotation.name();
		final ContentType contentType = clazz.getAnnotation(ContentType.class);
		if ( contentType!=null )
			return contentType.value();
		throw new UnsupportedOperationException( clazz +  " must be annotated with @" + ContentType.class.getSimpleName() );
	}
}
