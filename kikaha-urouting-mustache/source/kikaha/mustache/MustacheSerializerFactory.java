package kikaha.mustache;

import lombok.extern.slf4j.Slf4j;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

@Slf4j
@Singleton
public class MustacheSerializerFactory {

	@Provided
	ServiceProvider provider;

	private MustacheSerializer serializer;

	public MustacheSerializer serializer() {
		if ( serializer == null )
			synchronized ( this ) {
				if ( serializer == null )
					loadSerializer();
			}
		return serializer;
	}

	private void loadSerializer() {
		try {
			serializer = provider.load( MustacheSerializer.class );
		} catch ( ServiceProviderException e ) {
			log.error( "Can't load Mustache serializer", e );
			throw new RuntimeException( e );
		}
	}
}
