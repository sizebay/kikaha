package kikaha.urouting.jaxb;

import java.nio.file.Files;
import java.nio.file.Paths;

import lombok.SneakyThrows;

import org.junit.Before;

import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class TestCase {

	final ServiceProvider provider = new DefaultServiceProvider();

	@Before
	public void setup() throws ServiceProviderException {
		provider.provideOn( this );
	}

	@SneakyThrows
	protected String readFile( String fileName ) {
		final String fullFileName = String.format( "tests/META-INF/%s", fileName );
		final byte[] bytes = Files.readAllBytes( Paths.get( fullFileName ) );
		return new String( bytes );
	}

}