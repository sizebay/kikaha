package kikaha.mojo.generator;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.List;

public class Config {

	public List<File> sourceDir = asList( file( "source" ), file( "src/main/java" ) );
	public List<File> outputDir = asList( file( "target" ), file( "output" ) );
	public List<File> classOutputDir = outputDir;
	public List<File> classPath;

	private static File file( String path ) {
		return new File( path );
	}
}
