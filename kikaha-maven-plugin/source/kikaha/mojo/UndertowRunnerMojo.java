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
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;

/**
 * @goal run
 * @requiresDependencyResolution compile+runtime
 */
public class UndertowRunnerMojo extends AbstractMojo {

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
	 * @parameter default-value=""
	 */
	String profile;

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
			String commandLineString = getCommandLineString();
			System.out.println( "CML: " + commandLineString );
			run( commandLineString );
		} catch ( Exception e ) {
			throw new MojoExecutionException( "Can't initialize Undertow Server.", e );
		}
	}

	@SuppressWarnings( "unchecked" )
	void memorizeClassPathWithRunnableJar()
			throws DependencyResolutionRequiredException, ArtifactResolutionException, ArtifactNotFoundException {
		final List<String> artifactsInClassPath = new ArrayList<>();
		for ( Artifact artifact : (Set<Artifact>)this.project.getArtifacts() ) {
			final String artifactAbsolutePath = getArtifactAbsolutePath( artifact );
			if ( !artifactsInClassPath.contains( artifactAbsolutePath ) ) {
				this.classPath.append( artifactAbsolutePath ).append( SEPARATOR );
				artifactsInClassPath.add( artifactAbsolutePath );
			}
		}
		this.classPath.append( getFinalArtifactName() );
	}

	String resolveUndertowStadalone() throws ArtifactResolutionException, ArtifactNotFoundException {
		Artifact undertowStandalone = getUndertowStandalone();
		return getArtifactAbsolutePath( undertowStandalone );
	}

	String getArtifactAbsolutePath( Artifact artifact )
			throws ArtifactResolutionException, ArtifactNotFoundException {
		this.resolver.resolve( artifact, Collections.EMPTY_LIST, this.localRepository );
		return artifact.getFile().getAbsolutePath();
	}

	Artifact getUndertowStandalone() {
		return this.factory.createDependencyArtifact(
				this.plugin.getGroupId(), "undertow-standalone",
				VersionRange.createFromVersion( this.plugin.getVersion() ), "jar", "", Artifact.SCOPE_RUNTIME );
	}

	String getFinalArtifactName() {
		String fileName = String.format( "%s.%s", this.finalName, this.project.getPackaging() );
		return new File( this.buildDirectory, fileName ).getAbsolutePath();
	}

	String getCommandLineString() {
		return String.format(
				"java -cp '%s' %s %s",
				this.classPath.toString(),
				Main.class.getCanonicalName(),
				this.profile != null ? this.profile : "" );
	}

	void run( String commandLineString ) throws IOException, InterruptedException {
		final Runtime runtime = Runtime.getRuntime();
		final Process exec = runtime.exec( commandLineString );
		runtime.addShutdownHook( new ProcessDestroyer( exec ) );
		printAsynchronously( exec.getInputStream() );
		printAsynchronously( exec.getErrorStream() );
		if ( exec.waitFor() > 0 )
			throw new RuntimeException( "The Undertow Server has failed to run." );
	}

	void printAsynchronously( InputStream stream ) {
		new Thread( new ProcessOutputPrinter( stream ) ).start();
	}
}

@RequiredArgsConstructor
class ProcessDestroyer extends Thread {
	final Process process;

	@Override
	public void run() {
		process.destroy();
		System.out.println( "Undertow has shutting down!" );
	}
}