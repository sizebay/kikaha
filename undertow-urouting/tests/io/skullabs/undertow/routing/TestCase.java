package io.skullabs.undertow.routing;

import java.nio.file.Files;
import java.nio.file.Paths;

import lombok.SneakyThrows;

public class TestCase {

	@SneakyThrows
	protected String readFile( String fileName ) {
		String fullFileName = String.format( "tests/META-INF/%s", fileName );
		byte[] bytes = Files.readAllBytes( Paths.get( fullFileName ) );
		return new String( bytes );
	}

}