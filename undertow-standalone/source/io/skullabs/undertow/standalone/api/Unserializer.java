package io.skullabs.undertow.standalone.api;

import java.io.Reader;

public interface Unserializer {

	<T> T unserialize( Reader input, Class<T> targetClass );
}
