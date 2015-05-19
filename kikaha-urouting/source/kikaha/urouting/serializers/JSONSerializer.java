package kikaha.urouting.serializers;

import java.io.IOException;
import java.io.OutputStream;

import kikaha.urouting.api.AbstractSerializer;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Serializer;
import trip.spi.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton( name = Mimes.JSON, exposedAs = Serializer.class )
public class JSONSerializer extends AbstractSerializer {
	
	final ObjectMapper mapper = Jackson.createMapper();

	@Override
	public <T> void serialize( final T object, final OutputStream output ) throws IOException {
		mapper.writeValue( output, object );
	}
}
