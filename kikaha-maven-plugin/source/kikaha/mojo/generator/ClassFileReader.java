package kikaha.mojo.generator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;

public class ClassFileReader {

	final Map<String, Object> options = createDefaultOptions();
	final File rootDir;
	final boolean shouldRemoveFiles;

	public ClassFileReader( File targetDir, boolean shouldRemoveFiles ) {
		this.rootDir = targetDir;
		this.shouldRemoveFiles = shouldRemoveFiles;
	}

	private Map<String, Object> createDefaultOptions() {
		final Map<String, Object> options = new HashMap<String, Object>();
		options.put( IFernflowerPreferences.LOG_LEVEL, "warn" );
		options.put( IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, "1" );
		options.put( IFernflowerPreferences.REMOVE_SYNTHETIC, "1" );
		options.put( IFernflowerPreferences.REMOVE_BRIDGE, "1" );
		options.put( IFernflowerPreferences.LITERALS_AS_IS, "1" );
		options.put( IFernflowerPreferences.UNIT_TEST_MODE, "1" );
		return options;
	}

	public StringJavaSource decompile( File classFile ) {
		final File packageFolder = getOutputLocationFor( classFile.getParentFile() );
		final File targetDir = new File( rootDir.getAbsolutePath(), packageFolder.getPath() );
		decompile( classFile, targetDir );

		final String className = extractClassNameFromClassFile( classFile );
		final File decompiledFile = new File( targetDir.getAbsolutePath(), className + ".java" );

		final String canonicalName = packageFolder.getPath().replaceAll( "[/\\\\]", "." ) + "." + className;
		return readFileAndRemove( canonicalName, decompiledFile );
	}

	private void decompile( File classFile, final File targetDir ) {
		final ConsoleDecompiler decompiler = new ConsoleDecompiler( targetDir, options );
		for ( final File file : collectClasses( classFile ) )
			decompiler.addSpace( file, true );
		decompiler.decompileContext();
	}

	private File getOutputLocationFor( File file ) {
		final String s = file.getAbsolutePath()
				.replaceAll( "\\\\", "/" )
				.replaceFirst( "^" + rootDir.getAbsolutePath().replaceAll( "\\\\", "/" ), "" )
				.replaceFirst( "^/", "" );
		return new File( s );
	}

	private static String extractClassNameFromClassFile(File classFile ) {
		final String absolutePath = classFile.getName();
		return absolutePath.substring( 0, absolutePath.length() - 6 );
	}

	private StringJavaSource readFileAndRemove( String canonicalName, File decompiledFile ) {
		try {
			final byte[] bytes = Files.readAllBytes( Paths.get( decompiledFile.getAbsolutePath() ) );
			if ( shouldRemoveFiles && !decompiledFile.delete() )
				throw new IllegalStateException( "Can't remove the file " + decompiledFile );
			final String source = new String( bytes );
			return new StringJavaSource( canonicalName, source );
		} catch ( final IOException e ) {
			throw new IllegalStateException( "Can't read the file " + decompiledFile, e );
		}
	}

	private static List<File> collectClasses( File classFile ) {
		final List<File> files = new ArrayList<File>();
		files.add( classFile );

		final File parent = classFile.getParentFile();
		if ( parent != null ) {
			final String pattern = classFile.getName().replace( ".class", "" ) + "\\$.+\\.class";
			final File[] inner = parent.listFiles( new PatternFilter( pattern ) );
			if ( inner != null )
				Collections.addAll( files, inner );
		}

		return files;
	}
}

class PatternFilter implements FilenameFilter {

	final String pattern;

	public PatternFilter( String pattern ) {
		this.pattern = pattern;
	}

	@Override
	public boolean accept( File dir, String name ) {
		return name.matches( pattern );
	}
}