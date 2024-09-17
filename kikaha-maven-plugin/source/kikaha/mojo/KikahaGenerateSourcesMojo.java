package kikaha.mojo;

import kikaha.mojo.generator.*;
import kikaha.mojo.generator.ClassFileReader.SourceNotCompiledException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;

@Mojo( name = "generate-sources",
		defaultPhase = LifecyclePhase.COMPILE,
		requiresDependencyResolution = ResolutionScope.TEST )
public class KikahaGenerateSourcesMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", required = true)
	MavenProject project;

	@Parameter( defaultValue = "${project.build.outputDirectory}", required = true )
	public String compileClassesDirectory;

	@Parameter( defaultValue = "${project.testClasspathElements}", readonly = true, required = true )
	public List<String> testClasspathElements;

	@Parameter( defaultValue = "${project.compileClasspathElements}", readonly = true, required = true )
	public List<String> classpathElements;

	@Parameter( defaultValue = "true", required = true )
	public boolean shouldRemoveFiles;

	@Parameter( defaultValue = "false", required = true)
	public boolean force;

	ClassFileReader decompiler;
	SimplifiedAPTRunner runner;
	Config config;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if ( !project.getPackaging().equals( "jar" ) && !force ) return;
		readConfiguration();
		ensureSourceDirectoriesExists();
		configRunnerAndDecompiler();

		final List<StringJavaSource> decompiledSources = decompileSources();
		if ( decompiledSources.size() > 0 )
			generateSources( decompiledSources );
	}

	private void readConfiguration() {
		final HashSet<String> safeClassPath = new HashSet<>();
		safeClassPath.addAll( classpathElements );
		safeClassPath.addAll( testClasspathElements );

		config = new Config();
		config.classPath = safeClassPath.stream().map( s -> new File( s ) ).collect( Collectors.toList() );
		config.sourceDir = asList( new File( compileClassesDirectory ) );
		config.outputDir = asList( new File( compileClassesDirectory ) );
		config.classOutputDir = asList( new File( compileClassesDirectory ) );
	}

	private void ensureSourceDirectoriesExists() throws MojoFailureException {
		final File directory = new File( compileClassesDirectory );
		if ( !directory.exists() && !directory.mkdirs() )
			throw new MojoFailureException( "Cannot create " + compileClassesDirectory + "directory." );
	}

	private void configRunnerAndDecompiler() {
		decompiler = new ClassFileReader( new File( compileClassesDirectory ), shouldRemoveFiles );
		runner = new SimplifiedAPTRunner( config, ToolProvider.getSystemJavaCompiler() );
	}

	private List<StringJavaSource> decompileSources() throws MojoExecutionException {
		final List<File> javaClassFiles = readJavaClassFiles();
		final List<StringJavaSource> decompiledSources = new ArrayList<>();
		for ( final File file : javaClassFiles ) {
			try {
				final StringJavaSource decompiled = decompiler.decompile( file );
				decompiledSources.add( decompiled );
			} catch ( SourceNotCompiledException cause ) {
				getLog().warn( "Ignoring " + file + ". " + cause.getMessage() );
			}
		}
		return decompiledSources;
	}

	private void generateSources( final List<StringJavaSource> decompiledSources ) throws MojoFailureException {
		getLog().info("Running Annotation Processing Tool on " + decompiledSources.size() + " sources.");
		final APTResult result = runner.run(decompiledSources);
		for ( final Diagnostic<? extends JavaFileObject> d : result.diagnostics )
			if ( result.success )
				getLog().info( d.getMessage( Locale.ENGLISH ) );
			else showErrorMessage( d );
		if ( !result.success )
			throw new MojoFailureException( "Could not analyse the source code" );
	}

	private void showErrorMessage( final Diagnostic<? extends JavaFileObject> d ){
		String message = d.getMessage(Locale.ENGLISH);
		if ( d.getSource() != null )
			message+= format(" (%s:%d)", d.getSource().getName(), d.getLineNumber());
		if ( Diagnostic.Kind.ERROR.equals( d.getKind() ) )
			getLog().error( message );
		else if ( Diagnostic.Kind.MANDATORY_WARNING.equals( d.getKind() ) )
			getLog().warn( message );
		else
			getLog().info( message );
	}

	private List<File> readJavaClassFiles() throws MojoExecutionException {
		try {
			final Path rootDir = Paths.get( compileClassesDirectory );
			return Files.walk( rootDir )
					.filter( p -> p.toString().endsWith( ".class" ) )
					.unordered().map( p -> p.toFile() ).collect( Collectors.toList() );
		} catch ( final IOException e ) {
			throw new MojoExecutionException( "Can't read java classes", e );
		}
	}
}
