package kikaha.mojo;

import java.io.File;
import com.amazonaws.auth.*;
import com.amazonaws.services.s3.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 */
@Mojo( name = "deploy-on-aws-s3", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME )
public class KikahaS3DeployerMojo extends AbstractMojo {

	final AWSCredentialsProviderChain credentials = DefaultAWSCredentialsProviderChain.getInstance();

	@Parameter( defaultValue = "false", required = true )
	Boolean enabled;

	@Parameter( defaultValue = "${project.build.finalName}-runnable.jar", required = true )
	String jarFileName;

	@Parameter( defaultValue = "${project.build.directory}", required = true )
	File targetDirectory;

	@Parameter( required = true )
	String s3Bucket;

	@Parameter( defaultValue = "${project.groupId}-${project.artifactId}", required = true )
	String s3Key;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if ( !enabled ) return;

		File packageFile = new File( targetDirectory.getAbsolutePath() + File.separatorChar + jarFileName );
		if ( !packageFile.exists() )
			throw new MojoFailureException( "Package not found: " + packageFile.getName() + ". Try execute kikaha:jar and try again." );

		getLog().info( "Deploying " + packageFile + " into AWS S3(" + s3Bucket + "/" + s3Key + ".jar)" );
		uploadPackage( packageFile );
	}

	void uploadPackage( File packageFile ) {
		final AmazonS3 s3 = AmazonS3Client.builder().withCredentials( credentials ).build();
		s3.putObject( s3Bucket, s3Key + ".jar", packageFile );
		AWS.await();
	}
}
