package kikaha.urouting.serializers.jackson;

import java.nio.file.*;
import lombok.SneakyThrows;

public class TestCase {

	@SneakyThrows
	static String readFile( String fileName ) {
		final String fullFileName = String.format( "tests-resources/META-INF/%s", fileName );
		final byte[] bytes = Files.readAllBytes( Paths.get( fullFileName ) );
		return new String( bytes );
	}

}