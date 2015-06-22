package kikaha.urouting.api;

import java.io.IOException;
import java.io.Reader;

public interface Unserializer {

	<T> T unserialize( final Reader input, final Class<T> targetClass ) throws IOException;
}
