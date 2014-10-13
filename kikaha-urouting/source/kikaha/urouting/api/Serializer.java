package kikaha.urouting.api;

import java.io.OutputStream;

public interface Serializer {

	<T> void serialize( final T object, final OutputStream output ) throws RoutingException;
}
