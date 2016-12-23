package kikaha.mojo;

import kikaha.core.cdi.ApplicationRunner;
import kikaha.mojo.runner.MainClassService;
import lombok.val;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mojo( name = "run",
	requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME )
public class KikahaRunnerMojo extends AbstractMojo {

	final static String SEPARATOR = System.getProperty( "path.separator" );

	@Parameter( defaultValue = "${project}", required = true)
	MavenProject project;

	@Parameter( defaultValue = "${project.basedir}/src/main/webapp", alias = "webResourcesPath", required = true)
	String webResourcesPath;

	@Parameter( defaultValue = "${localRepository}", required = true)
	ArtifactRepository localRepository;

	@Component
	ArtifactResolver resolver;

	@Parameter( defaultValue = "${project.build.finalName}", required = true)
	String finalName;

	@Parameter( defaultValue = "false", required = true)
	public boolean force;

	@Parameter( defaultValue = "-Xmx256m", required = true)
	String jvmArgs;

	@Parameter( defaultValue = "${project.build.outputDirectory}", required = true)
	File compiledClassesDir;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if ( !project.getPackaging().equals( "jar" ) && !force ) return;
		try {
			val classpath = memorizeClassPathWithRunnableJar();
			val log = getLog();
			val service = new MainClassService( compiledClassesDir, ApplicationRunner.class.getCanonicalName(), classpath,
					asList( "-Dserver.static.location=" + absolutePath(webResourcesPath) ), jvmArgs, log );
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