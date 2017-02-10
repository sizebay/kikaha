package kikaha.mojo.generator;

import java.io.*;
import java.util.*;
import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;

public class SimplifiedAPTRunner {

	final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	final List<String> compilerOptions = Arrays.asList();
	final List<String> compilerOptionsForProcOnly = Arrays.asList( "-proc:only" );

	final JavaCompiler compiler;
	final StandardJavaFileManager fileManager;
	final Config config;

	public SimplifiedAPTRunner(Config config, JavaCompiler compiler ) {
		this.config = config;
		this.compiler = compiler;
		this.fileManager = createFileManager();
	}

	public APTResult run( JavaFileObject... compilationUnits ) {
		final List<JavaFileObject> compilationUnitsAsList = Arrays.asList( compilationUnits );
		return run( compilationUnitsAsList );
	}

	public APTResult run( Iterable<? extends JavaFileObject> compilationUnits ) {
		final CompilationTask task = compiler.getTask( null, fileManager, diagnostics, compilerOptionsForProcOnly, null, compilationUnits );
		final boolean success = task.call();
		final List<Diagnostic<? extends JavaFileObject>> generatedDiagnostics = diagnostics.getDiagnostics();
		return new APTResult( success, generatedDiagnostics );
	}

	private StandardJavaFileManager createFileManager() {
		try {
			ensureThatConfigDirectoriesExists( config );
			final StandardJavaFileManager fileManager = compiler.getStandardFileManager( diagnostics, null, null );
			if ( config.classPath != null )
				fileManager.setLocation( StandardLocation.CLASS_PATH, config.classPath );
			fileManager.setLocation( StandardLocation.CLASS_OUTPUT, config.classOutputDir );
			fileManager.setLocation( StandardLocation.SOURCE_PATH, config.sourceDir );
			fileManager.setLocation( StandardLocation.SOURCE_OUTPUT, config.outputDir );
			return fileManager;
		} catch ( final IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	static void ensureThatConfigDirectoriesExists( Config config ) {
		ensureThatConfigDirectoryExists( config.sourceDir, false );
		ensureThatConfigDirectoryExists( config.outputDir, true );
		ensureThatConfigDirectoryExists( config.classOutputDir, true );
	}

	static void ensureThatConfigDirectoryExists( List<File> dirs, boolean forceCreate ) {
		for ( final File dir : dirs )
			ensureThatConfigDirectoryExists( dir, forceCreate );
	}

	static void ensureThatConfigDirectoryExists( File dir, boolean forceCreate ) {
		if ( !dir.exists() )
			if ( !forceCreate || !dir.mkdirs() )
				throw new IllegalStateException( "Directory does not exists (or could not be created): " + dir );
	}
}
