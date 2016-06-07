package kikaha.core.modules.websocket;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit tests for {@link WebSocketPlainTextSerializers}.
 */
public class WebSocketPlainTextSerializersTest {

	final WebSocketPlainTextSerializers serializers = new WebSocketPlainTextSerializers();
	final KnownObject knownObject = new KnownObject();

	@Test
	public void shouldBeAbleToSerializeData() throws Exception {
		final String serialized = serializers.serialize(knownObject);
		assertEquals( "KnownObject", serialized );
	}

	@Test
	public void shouldBeAbleToUnserializeString() throws Exception {
		final String unserialized = serializers.unserialize("String", String.class);
		assertEquals( "String", unserialized );
	}

	@Test( expected = UnsupportedOperationException.class )
	public void shouldFailToToUnserializeNonStringObjects() throws Exception {
		serializers.unserialize("KnownObject", KnownObject.class);
	}
}

class KnownObject {

	@Override
	public String toString() {
		return "KnownObject";
	}
}