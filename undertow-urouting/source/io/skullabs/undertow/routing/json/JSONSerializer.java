package io.skullabs.undertow.routing.json;

import java.io.IOException;
import java.io.Writer;

import trip.spi.Name;
import trip.spi.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.skullabs.undertow.routing.Mimes;
import io.skullabs.undertow.routing.RoutingException;
import io.skullabs.undertow.routing.Serializer;

@Service
@Name( Mimes.JSON )
public class JSONSerializer implements Serializer {
	
	final ObjectMapper mapper = new ObjectMapper();

	@Override
	public <T> void serialize(T object, Writer output) throws RoutingException {
		try {
			mapper.writeValue(output, object);
		} catch (IOException cause) {
			throw new RoutingException(cause);
		}
	}

}
