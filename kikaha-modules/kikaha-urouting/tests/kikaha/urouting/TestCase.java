package kikaha.urouting;

import java.nio.file.Files;
import java.nio.file.Paths;

import lombok.SneakyThrows;

public class TestCase {

	@SneakyThrows
	static String readFile( String fileName ) {
		final String fullFileName = String.format( "tests-resources/META-INF/%s", fileName );
		final byte[] bytes = Files.readAllBytes( Paths.get( fullFileName ) );
		return new String( bytes );
	}
}