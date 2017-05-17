package kikaha.mojo;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.codedeploy.*;
import com.amazonaws.services.codedeploy.model.*;
import com.amazonaws.services.s3.*;
import kikaha.mojo.packager.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.*;

/**
 *
 */
@Mojo( name = "deploy-on-aws-s3", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME )
public class KikahaS3DeployerMojo extends AbstractMojo {

	static final String DEFAULT_DIR = "META-INF/aws-code-deploy/";

	final AWSCredentialsProviderChain credentials = DefaultAWSCredentialsProviderChain.getInstance();

	@Parameter( defaultValue = "false", required = true )
	Boolean enabled;

	@Parameter( defaultValue = "false", required = true )
	Boolean useCodeDeploy;

	@Parameter( defaultValue = DEFAULT_DIR )
	String codeDeployFolder;

	@Parameter( defaultValue = "${project.groupId}-${project.artifactId}" )
	String codeDeployApplicationName;

	@Parameter( defaultValue = "production" )
	String codeDeployDeploymentGroupName;

	@Parameter( defaultValue = "${project.build.finalName}-runnable.jar", required = true )
	String jarFileName;

	@Parameter( defaultValue = "${project.build.directory}", required = true )
	File targetDirectory;

	@Parameter( defaultValue = "us-east-1", required = true )
	String regionName;

	@Parameter( required = true )
	String s3Bucket;

	@Parameter( defaultValue = "${project.groupId}-${project.artifactId}", required = true )
	String s3Key;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if ( !enabled ) return;

		File packageFile = useCodeDeploy ? createCodeDeployZipFile() : getJarFile();
		if ( !packageFile.exists() )
			throw new MojoFailureException( "Package not found: " + packageFile.getName() + ". Try execute kikaha:jar and try again." );

		getLog().info( "Deploying package on AWS S3: " + s3Bucket + "/" + s3Key );
		uploadPackage( packageFile );
		if ( useCodeDeploy )
			deployPackage();
	}

	void uploadPackage( File packageFile ) {
		final AmazonS3 s3 = AmazonS3Client.builder().withCredentials( credentials )
				.withRegion( Regions.fromName(regionName) ).build();
		s3.putObject( s3Bucket, s3Key, packageFile );
	}

	void deployPackage() {
		final AmazonCodeDeploy codeDeploy = AmazonCodeDeployClient.builder().withCredentials(credentials)
				.withRegion(Regions.fromName(regionName)).build();
		final S3Location s3Location = new S3Location().withBucket(s3Bucket).withKey(s3Key)
				.withBundleType(BundleType.Zip);
		final CreateDeploymentRequest createDeploymentRequest = new CreateDeploymentRequest()
				.withApplicationName( codeDeployApplicationName )
				.withDeploymentGroupName( codeDeployDeploymentGroupName )
				.withRevision(new RevisionLocation().withS3Location(s3Location).withRevisionType(RevisionLocationType.S3));
		final CreateDeploymentResult result = codeDeploy.createDeployment(createDeploymentRequest);
		getLog().info( result.toString() );
	}

	File getJarFile(){
		return new File( targetDirectory.getAbsolutePath() + File.separatorChar + jarFileName );
	}

	File createCodeDeployZipFile() throws MojoExecutionException {
		final String fileName = targetDirectory.getAbsolutePath() + File.separatorChar + s3Key + ".zip";
		final File file = new File( fileName );
		getLog().info( "Creating deployment package for AWS CodeDeploy at " + file.getAbsolutePath() );

		final ZipFileWriter zipFile = createZipFile(fileName);
		copyFileToZip( zipFile, getJarFile(), "lib/application.jar" );

		if ( DEFAULT_DIR.equals(codeDeployFolder) )
			copyFilesFromPluginJarToZip(zipFile);
		else
			copyCodeDeployFolderFolderToZip( zipFile );

		zipFile.close();
		return file;
	}

	ZipFileWriter createZipFile( final String fileName ) throws MojoExecutionException {
		final ZipFileWriter zipFile = new ZipFileWriter( fileName );
		zipFile.stripPrefix( codeDeployFolder );
		return zipFile;
	}

	void copyFilesFromPluginJarToZip( final ZipFileWriter zip ) throws MojoExecutionException {
		final CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
		final URL location = codeSource.getLocation();
		final String jar = location.getPath();
		copyFilesFromJarToZip( zip, jar );
	}

	void copyFilesFromJarToZip( final ZipFileWriter zip, final String path ) throws MojoExecutionException {
		try ( final ZipFileReader reader = new ZipFileReader( path.replace( "%20", " " ) ) ) {
			reader.read((name, content) -> {
				if (name.startsWith(DEFAULT_DIR))
					zip.add(name, content);
			});
		} catch ( IOException cause ) {
			throw new MojoExecutionException( "Can't copy file to zip file", cause );
		}
	}

	void copyCodeDeployFolderFolderToZip( final ZipFileWriter zip ) throws MojoExecutionException {
		final File directory = new File(codeDeployFolder);
		copyDirectoryFilesToZip( zip, directory );
	}

	void copyDirectoryFilesToZip( final ZipFileWriter zip, final File directory ) throws MojoExecutionException {
		if ( directory.exists() )
			for ( final File file : directory.listFiles() )
				copyToZip( zip, file );
	}

	void copyToZip( final ZipFileWriter zip, final File file ) throws MojoExecutionException {
		if ( file.isDirectory() )
			copyDirectoryFilesToZip( zip, file );
		else {
			final String fileName = ( file.getName() ).replaceFirst( "^/", "" );
			copyFileToZip( zip, file, fileName );
		}
	}

	void copyFileToZip( final ZipFileWriter zip, final File file, final String fileName ) throws MojoExecutionException {
		try {
			final InputStream content = new FileInputStream(file);
			zip.add(fileName, content);
		} catch ( IOException cause ) {
			throw new MojoExecutionException( "Failed to copy file to zip", cause );
		}
	}
}
