package kikaha.urouting.jaxb;

import kikaha.core.test.KikahaRunner;
import kikaha.urouting.SerializerAndUnserializerProvider;
import kikaha.urouting.api.AbstractSerializer;
import kikaha.urouting.api.AbstractUnserializer;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.jaxb.User.Address;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import static kikaha.urouting.jaxb.TestCase.readFile;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(KikahaRunner.class)
public class SerializationTest {

	final User user = new User( "gerolasdiwn",
			new Address( "Madison Avenue", 10 ) );

	@Inject
	SerializerAndUnserializerProvider provider;

	@Test
	@SneakyThrows
	public void grantThatSerializeItAsXML() {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final AbstractSerializer serializer = (AbstractSerializer)provider.getSerializerFor(Mimes.XML );
		serializer.serialize( user, outputStream );
		final String expected = readFile("serialization.expected-xml.xml");
		assertThat( outputStream.toString(), is( expected ) );
	}

	@Test
	@SneakyThrows
	public void grantThatUnserializeXMLIntoObjectAsExpected() {
		final String xml = readFile("serialization.expected-xml.xml");
		final AbstractUnserializer unserializer = (AbstractUnserializer)provider.getUnserializerFor( Mimes.XML );
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
