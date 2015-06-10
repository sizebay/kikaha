package kikaha.urouting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import kikaha.urouting.User.Address;
import kikaha.urouting.api.AbstractSerializer;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Serializer;
import kikaha.urouting.api.Unserializer;
import lombok.SneakyThrows;

import org.junit.Test;

import trip.spi.ServiceProvider;

public class SerializationTests extends TestCase {

	final ServiceProvider provider = new ServiceProvider();
	final User user = new User( "gerolasdiwn",
			new Address( "Madison Avenue", 10 ) );

	@Test
	@SneakyThrows
	public void grantThatSerializeItAsXML() {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final AbstractSerializer serializer = (AbstractSerializer)provider.load( Serializer.class, Mimes.XML );
		serializer.serialize( user, outputStream );
		final String expected = readFile( "serialization.expected-xml.xml" );
		assertThat( outputStream.toString(), is( expected ) );
	}

	@Test
	@SneakyThrows
	public void grantThatUnserializeXMLIntoObjectAsExpected() {
		final String xml = readFile( "serialization.expected-xml.xml" );
		final Unserializer unserializer = provider.load( Unserializer.class, Mimes.XML );
		final User user = unserializer.unserialize( new StringReader( xml ), User.class );
		assertIsValidUser( user );
	}

	void assertIsValidUser( final User user ) {
		assertNotNull( user );
		assertThat( user.name, is( "gerolasdiwn" ) );
		assertNotNull( user.addresses );
		assertThat( user.addresses.size(), is( 1 ) );
		final Address address = user.addresses.get( 0 );
		assertThat( address.street, is( "Madison Avenue" ) );
		assertThat( address.number, is( 10 ) );
	}
}
