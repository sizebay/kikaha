package kikaha.mojo;

import java.io.File;
import java.util.*;
import kikaha.core.cdi.ApplicationRunner;
import kikaha.mojo.runner.MainClassService;
import lombok.val;
import org.apache.maven.artifact.*;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.*;
import org.apache.maven.plugin.*;
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

	/** @parameter default-value="${localRepository}" */
	ArtifactRepository localRepository;

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
	 * Compiled classes directory.
	 *
	 * @parameter expression="${project.build.directory}/classes"
	 * @required
	 */
	File compiledClassesDir;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			val classpath = memorizeClassPathWithRunnableJar();
			val log = getLog();
			val service = new MainClassService( compiledClassesDir, ApplicationRunner.class.getCanonicalName(), classpath,
					asList( "-Dserver.static.location=" + absolutePath( webresourcesPath ) ), jvmArgs, log );
			val process = service.start();
			if ( process.waitFor() > 0 )
				throw new RuntimeException( "Kikaha has unexpectedly finished." );
		} catch ( final Exception e ) {
			throw new MojoExecutionException( "Can't initialize Kikaha.", e );
		}
	}

	String absolutePath( String str ) {
		final String absolutePath = new File(str).getAbsolutePath();
		return absolutePath;
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
		artifactsInClassPath.add( compiledClassesDir.getAbsolutePath() );
		return artifactsInClassPath;
	}

	String getArtifactAbsolutePath( final Artifact artifact )
			throws ArtifactResolutionException, ArtifactNotFoundException {
		this.resolver.resolve( artifact, Collections.EMPTY_LIST, this.localRepository );
		return artifact.getFile().getAbsolutePath();
	}
}