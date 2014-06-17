package io.skullabs.undertow.urouting;

import java.io.Reader;

public interface Unserializer {

	<T> T unserialize( Reader input, Class<T> targetClass ) throws RoutingException;
}
