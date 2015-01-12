package kikaha.urouting.serializers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import kikaha.urouting.api.AbstractSerializer;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Serializer;
import trip.spi.Singleton;

@Singleton( name = Mimes.PLAIN_TEXT, exposedAs = Serializer.class )
public class PlainTextSerializer extends AbstractSerializer {

	static final String NULL = "";

	@Override
	public <T> void serialize( final T object, final OutputStream output ) throws RoutingException {
		try {
			if ( object != null ) {
				final OutputStreamWriter writer = new OutputStreamWriter( output );
				writer.write( object.toString() );
				writer.close();
			}
		} catch ( final IOException cause ) {
			throw new RoutingException( cause );
		}
	}
}
