package io.skullabs.undertow.urouting;

import java.io.Writer;

public interface Serializer {

	<T> void serialize( T object, Writer output ) throws RoutingException;
}
