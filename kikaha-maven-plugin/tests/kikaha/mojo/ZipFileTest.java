package kikaha.mojo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class ZipFileTest {

	static final String OUTPUT_GENERATED_ZIP = "output/generated.zip";

	@Test
	public void ensureNothing() {
		final CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
		final URL location = codeSource.getLocation();
		assertNotNull( location );
	}

	@Test
	@SneakyThrows
	public void zipCreationTest() {
		final ZipFileWriter zip = new ZipFileWriter( OUTPUT_GENERATED_ZIP, "generated" );
		zip.stripPrefix( "META-INF/" );
		zip.add( "META-INF/conf/application.conf", inputStreamFrom( "hello: \"world\"" ) );
		zip.close();

		final List<String> names = readFileNames( OUTPUT_GENERATED_ZIP );
		assertThat( names.size(), is( 1 ) );
		assertThat( names.get( 0 ), is( "generated/conf/application.conf" ) );
	}

	List<String> readFileNames( final String zipFileName ) throws FileNotFoundException, IOException, MojoExecutionException {
		final List<String> names = new ArrayList<>();
		final ZipFileReader reader = new ZipFileReader( zipFileName );
		reader.read( ( name, stream ) -> names.add( name ) );
		reader.close();
		return names;
	}

	InputStream inputStreamFrom( final String string ) {
		final byte[] bytes = string.getBytes();
		return new ByteArrayInputStream( bytes );
	}
}