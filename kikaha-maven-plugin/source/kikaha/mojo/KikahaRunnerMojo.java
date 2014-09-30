package kikaha.mojo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import kikaha.core.Main;
import lombok.RequiredArgsConstructor;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;

/**
 * @goal run
 * @requiresDependencyResolution compile+runtime
 */
public class KikahaRunnerMojo extends AbstractMojo {

	final static String SEPARATOR = System.getProperty( "path.separator" );

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	MavenProject project;

	/**
	 * The profile configuration to load when running the server.
	 *
	 * @parameter default-value="${project.basedir}/src/main/webapp"
	 */
	String webresourcesPath;

	/**
	 * @parameter default-value="${plugin}"
	 */
	PluginDescriptor plugin;

	/** @parameter default-value="${localRepository}" */
	ArtifactRepository localRepository;

	/**
	 * Used to construct artifacts for deletion/resolution...
	 *
	 * @component
	 */
	ArtifactFactory factory;

	/**
	 * @component
	 */
	ArtifactResolver resolver;

	/**
	 * Name of the generated JAR.
	 *
	 * @parameter alias="jarName" expression="${jar.finalName}"
	 *            default-value="${project.build.finalName}"
	 * @required
	 */
	String finalName;

	/**
	 * Directory containing the build files.
	 *
	 * @parameter expression="${project.build.directory}"
	 */
	File buildDirectory;

	StringBuilder classPath = new StringBuilder();
	String standaloneJar;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			memorizeClassPathWithRunnableJar();
			final String commandLineString = getCommandLineString();
			System.out.println( "CML: " + commandLineString );
			run( commandLineString );
		} catch ( final Exception e ) {
			throw new MojoExecutionException( "Can't initialize Kikaha.", e );
		}
	}

	@SuppressWarnings( "unchecked" )
	void memorizeClassPathWithRunnableJar()
			throws DependencyResolutionRequiredException, ArtifactResolutionException, ArtifactNotFoundException {
		final List<String> artifactsInClassPath = new ArrayList<>();
		for ( final Artifact artifact : (Set<Artifact>)this.project.getArtifacts() ) {
			final String artifactAbsolutePath = getArtifactAbsolutePath( artifact );
			if ( !artifactsInClassPath.contains( artifactAbsolutePath ) ) {
				this.classPath.append( artifactAbsolutePath ).append( SEPARATOR );
				artifactsInClassPath.add( artifactAbsolutePath );
			}
		}
		this.classPath
			.append( getFinalArtifactName() )
			.append( SEPARATOR ).append( "." );
	}

	String getArtifactAbsolutePath( final Artifact artifact )
			throws ArtifactResolutionException, ArtifactNotFoundException {
		this.resolver.resolve( artifact, Collections.EMPTY_LIST, this.localRepository );
		return artifact.getFile().getAbsolutePath();
	}

	String getFinalArtifactName() {
		final String fileName = String.format( "%s.%s", this.finalName, this.project.getPackaging() );
		return new File( this.buildDirectory, fileName ).getAbsolutePath();
	}

	String getCommandLineString() {
		return String.format(
			"java -cp \"%s\" %s \"%s\"",
				this.classPath.toString(),
				Main.class.getCanonicalName(),
				this.webresourcesPath != null ? this.webresourcesPath : "" );
	}

	void run( final String commandLineString ) throws IOException, InterruptedException {
		final Runtime runtime = Runtime.getRuntime();
		final Process exec = runtime.exec( commandLineString );
		runtime.addShutdownHook( new ProcessDestroyer( exec ) );
		printAsynchronously( exec.getInputStream() );
		printAsynchronously( exec.getErrorStream() );
		if ( exec.waitFor() > 0 )
			throw new RuntimeException( "The Kikaha has failed to run." );
	}

	void printAsynchronously( final InputStream stream ) {
		new Thread( new ProcessOutputPrinter( stream ) ).start();
	}
}

@RequiredArgsConstructor
class ProcessDestroyer extends Thread {
	final Process process;

	@Override
	public void run() {
		process.destroy();
		System.out.println( "Kikaha has shutting down!" );
	}
}