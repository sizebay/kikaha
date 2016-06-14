package kikaha.urouting.serializers.jackson;

import static kikaha.urouting.serializers.jackson.TestCase.readFile;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import java.io.IOException;
import javax.inject.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for {@link JSONWebSocketSerializer}.
 */
@RunWith(KikahaRunner.class)
public class JSONWebSocketSerializerTest {

	@Inject JSONWebSocketSerializer serializer;

	@Test
	public void ensureThatIsAbleToSerialize() throws JsonProcessingException {
		final User user = new User( "gerolasdiwn", new User.Address( "Madison Avenue", 10 ) );
		final String serialize = serializer.serialize(user);
		final String expected = readFile( "serialization.expected-json.json" );
		assertEquals( expected, serialize );
	}

	@Test
	public void ensureThatIsAbleToUnserialize() throws IOException {
		final String content = readFile( "serialization.expected-json.json" );
		final User user = serializer.unserialize(content, User.class);
		assertIsValidUser( user );
	}

	void assertIsValidUser( final User user ) {
		assertNotNull( user );
		assertThat( user.name, is( "gerolasdiwn" ) );
		assertNotNull( user.addresses );
		assertThat( user.addresses.size(), is( 1 ) );
		final User.Address address = user.addresses.get( 0 );
		assertThat( address.street, is( "Madison Avenue" ) );
		assertThat( address.number, is( 10 ) );
	}
}
