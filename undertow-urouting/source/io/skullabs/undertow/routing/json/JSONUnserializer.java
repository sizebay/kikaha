package io.skullabs.undertow.routing.json;

import java.io.IOException;
import java.io.Reader;

import trip.spi.Name;
import trip.spi.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.skullabs.undertow.routing.Mimes;
import io.skullabs.undertow.routing.RoutingException;
import io.skullabs.undertow.routing.Unserializer;

@Service
@Name( Mimes.JSON )
public class JSONUnserializer implements Unserializer {
	
	final ObjectMapper mapper = new ObjectMapper();

	@Override
	public <T> T unserialize(Reader input, Class<T> targetClass) throws RoutingException {
		try {
			return mapper.readValue(input, targetClass);
		} catch ( IOException cause ) {
			throw new RoutingException(cause);
		}
	}
}
