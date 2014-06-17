package io.skullabs.undertow.urouting.json;

import io.skullabs.undertow.urouting.Mimes;
import io.skullabs.undertow.urouting.RoutingException;
import io.skullabs.undertow.urouting.Unserializer;

import java.io.IOException;
import java.io.Reader;

import trip.spi.Name;
import trip.spi.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

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
