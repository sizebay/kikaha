package kikaha.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import kikaha.core.cdi.ApplicationRunner;
import kikaha.mojo.runner.MainClassService;
import lombok.val;

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
	 * JVM args to sent to Kikaha.
	 *
	 * @parameter  expression=" -Xms64m -Xmx256m "
	 * 			   default-value=" -Xms64m -Xmx256m "
	 * @required
	 */
	String jvmArgs;

	/**
	 * Directory containing the build files.
	 *
	 * @parameter expression="${project.build.directory}"
	 */
	File buildDirectory;

	/**
	 * Directory containing the build files.
	 *
	 * @parameter expression="${project.build.resources[0].directory}"
	 */
	File resourceDirectory;

	StringBuilder classPath = new StringBuilder();
	String standaloneJar;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			val classpath = memorizeClassPathWithRunnableJar();
			val service = new MainClassService( getWorkdirectory(), ApplicationRunner.class.getCanonicalName(), classpath,
					asList( "-Dserver.static.location=" + absolutePath( webresourcesPath ) ), jvmArgs );
			val process = service.start();
			if ( process.waitFor() > 0 )
				throw new RuntimeException( "Kikaha has unexpectedly finished." );
		} catch ( final Exception e ) {
			throw new MojoExecutionException( "Can't initialize Kikaha.", e );
		}
	}

	File getWorkdirectory(){
		if ( !resourceDirectory.exists() ){
			val file = new File("");
			return new File( file.getAbsolutePath() );
		}
		return resourceDirectory;
	}

	String absolutePath( String str ) {
		return new File( str ).getAbsolutePath();
	}

	List<String> asList( final String...strings ){
		val list = new ArrayList<String>();
		for ( val string : strings )
			list.add( string );
		return list;
	}

	@SuppressWarnings( "unchecked" )
	List<String> memorizeClassPathWithRunnableJar()
			throws DependencyResolutionRequiredException, ArtifactResolutionException, ArtifactNotFoundException {
		val artifactsInClassPath = new ArrayList<String>();
		for ( val artifact : (Set<Artifact>)this.project.getArtifacts() ) {
			val artifactAbsolutePath = getArtifactAbsolutePath( artifact );
			if ( !artifactsInClassPath.contains( artifactAbsolutePath ) )
				artifactsInClassPath.add( artifactAbsolutePath );
		}
		artifactsInClassPath.add( getFinalArtifactName() );
		artifactsInClassPath.add( resourceDirectory.getAbsolutePath() );
		return artifactsInClassPath;
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
}