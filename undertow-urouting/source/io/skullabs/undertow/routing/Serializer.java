package io.skullabs.undertow.routing;

import java.io.Writer;

public interface Serializer {

	<T> void serialize( T object, Writer output ) throws RoutingException;
}
