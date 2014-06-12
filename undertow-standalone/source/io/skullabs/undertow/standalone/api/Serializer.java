package io.skullabs.undertow.standalone.api;

import java.io.Writer;

public interface Serializer {

	<T> void serialize( T object, Writer output );
}
