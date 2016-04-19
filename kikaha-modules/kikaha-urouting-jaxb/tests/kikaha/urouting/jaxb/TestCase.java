package kikaha.urouting.jaxb;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestCase {

	@SneakyThrows
	static String readFile( String fileName ) {
		final String fullFileName = String.format( "tests-resources/%s", fileName );
		final byte[] bytes = Files.readAllBytes( Paths.get( fullFileName ) );
		return new String( bytes );
	}

}