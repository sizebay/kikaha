package kikaha.mojo;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import java.io.*;
import javax.tools.*;
import kikaha.mojo.generator.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.*;

public class SimplifiedAPTRunnerTest {

	static final String SOURCE_DIR = "tests-resources";
	static final String OUTPUT_DIR = "output";

	final ClassFileReader reader = new ClassFileReader( new File( SOURCE_DIR ), false );
	SimplifiedAPTRunner runner;

	@Before
	public void setup() {
		final Config config = new Config();
		config.sourceDir = asList( new File( SOURCE_DIR ) );
		config.outputDir = asList( new File( OUTPUT_DIR ) );
		config.classOutputDir = asList( new File( OUTPUT_DIR ) );
		runner = new SimplifiedAPTRunner( config, ToolProvider.getSystemJavaCompiler() );
	}

	@Test
	public void example() throws IOException, MojoExecutionException {
		final File compiledFile = new File( SOURCE_DIR, "kikaha/mojo/sample/User.class");
		final StringJavaSource decompiled = reader.decompile( compiledFile );
		final APTResult result = runner.run( decompiled );
		printErrorsIfAny( result );
		assertTrue( result.success );
		final int size = result.diagnostics.size();
		assertTrue( "Expected 3~5 warnings, but only " + size + " was found", size >= 3 && size < 5 );
	}

	private void printErrorsIfAny( final APTResult result ) {
		for ( final Diagnostic<? extends JavaFileObject> diagnostic : result.diagnostics )
			System.err.println( diagnostic );
	}
}