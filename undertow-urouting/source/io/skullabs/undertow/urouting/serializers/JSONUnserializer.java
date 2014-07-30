package io.skullabs.undertow.urouting.serializers;

import io.skullabs.undertow.urouting.api.Mimes;
import io.skullabs.undertow.urouting.api.RoutingException;
import io.skullabs.undertow.urouting.api.Unserializer;

import java.io.IOException;
import java.io.Reader;

import trip.spi.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton( name = Mimes.JSON )
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
