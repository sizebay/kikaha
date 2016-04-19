package kikaha.urouting.serializers.jackson;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestCase {

	@SneakyThrows
	static String readFile( String fileName ) {
		final String fullFileName = String.format( "tests/META-INF/%s", fileName );
		final byte[] bytes = Files.readAllBytes( Paths.get( fullFileName ) );
		return new String( bytes );
	}

}