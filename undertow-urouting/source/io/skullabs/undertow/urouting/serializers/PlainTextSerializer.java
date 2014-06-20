package io.skullabs.undertow.urouting.serializers;

import io.skullabs.undertow.urouting.Mimes;

import java.io.IOException;
import java.io.Writer;

import trip.spi.Name;
import trip.spi.Service;
import urouting.api.RoutingException;
import urouting.api.Serializer;

@Service
@Name( Mimes.PLAIN_TEXT )
public class PlainTextSerializer implements Serializer {

	static final String NULL = "null";

	@Override
	public <T> void serialize( T object, Writer output ) throws RoutingException {
		try {
			String serialized = object != null ? object.toString() : NULL;
			output.write( serialized );
		} catch ( IOException cause ) {
			throw new RoutingException( cause );
		}
	}
}
