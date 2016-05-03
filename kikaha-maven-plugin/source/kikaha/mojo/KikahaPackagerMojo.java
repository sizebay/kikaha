package kikaha.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.Cleanup;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * The Packager Mojo.
 *
 * @goal package
 * @requiresDependencyResolution compile+runtime
 * @author Miere Teixeira
 */
public class KikahaPackagerMojo extends AbstractMojo {

	static final String DEFAULT_DIR = "META-INF/defaults/";
	static final String METAINF_DIR = "META-INF";

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
	 * Directory containing the build files.
	 *
	 * @parameter expression="${project.build.directory}"
	 */
	File targetDirectory;

	/**
	 * Directory containing the build files.
	 *
	 * @parameter expression="${project.build.resources[0].directory}"
	 */
	File resourceDirectory;

	/**
	 * Name of the generated JAR.
	 *
	 * @parameter alias="jarName" expression="${jar.finalName}"
	 *            default-value="${project.build.finalName}"
	 * @required
	 */
	String finalName;

	/**
	 * @component
	 */
	ArtifactResolver resolver;

	/**
	 * @parameter default-value="${localRepository}"
	 */
	ArtifactRepository localRepository;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		ensureTargetDirectoryExists();
		final ZipFileWriter zip = createZipFile();
		populateZip( zip );
		zip.close();
		getLog().info( "Success: Zip file generated at " + zip.fileName );
	}

	void ensureTargetDirectoryExists() throws MojoFailureException {
		if ( !targetDirectory.exists() )
			if ( !targetDirectory.mkdirs() )
				throw new MojoFailureException( "Target Directory not created" );
	}

	ZipFileWriter createZipFile() throws MojoExecutionException {
		final String fileName = targetDirectory.getAbsolutePath() + File.separatorChar + finalName + ".zip";
		final ZipFileWriter zipFile = new ZipFileWriter( fileName, finalName );
		zipFile.stripPrefix( DEFAULT_DIR, METAINF_DIR );
		return zipFile;
	}

	void populateZip( final ZipFileWriter zip ) throws MojoExecutionException {
		try {
			copyFilesFromPluginJarToZip( zip );
			copyDependenciesToZip( zip );
			copyFinalArtifactToZip( zip );
			copyWebResourceFolderToZip( zip );
			copyDirectoryFilesToZip( zip, resourceDirectory, "" );
		} catch ( final IOException | ArtifactResolutionException | ArtifactNotFoundException e ) {
			throw new MojoExecutionException( "Failed to populate zip", e );
		}
	}

	void copyFilesFromPluginJarToZip( final ZipFileWriter zip ) throws MojoExecutionException, IOException {
		final CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
		final URL location = codeSource.getLocation();
		final String jar = location.getPath();
		copyFilesFromJarToZip( zip, jar );
	}

	void copyFilesFromJarToZip( final ZipFileWriter zip, final String path ) throws MojoExecutionException, IOException {
		final ZipFileReader reader = new ZipFileReader( path.replace( "%20", " " ) );
		try {
			reader.read( ( name, content ) -> {
				if ( name.startsWith( DEFAULT_DIR ) )
					zip.add( name, content );
			} );
		} finally {
			reader.close();
		}
	}

	@SuppressWarnings( "unchecked" )
	void copyDependenciesToZip( final ZipFileWriter zip )
			throws ArtifactResolutionException, ArtifactNotFoundException, MojoExecutionException, IOException
	{
		final Set<String> namesAlreadyIncludedToZip = new HashSet<>();
		for ( final Artifact artifact : (Set<Artifact>)project.getArtifacts() ) {
			final String artifactAbsolutePath = getArtifactAbsolutePath( artifact );
			if ( !namesAlreadyIncludedToZip.contains( artifactAbsolutePath ) ) {
				copyDependencyToZip( zip, artifact, artifactAbsolutePath );
				namesAlreadyIncludedToZip.add( artifactAbsolutePath );
			}
		}
	}

	void copyDependencyToZip(
			final ZipFileWriter zip,
			final Artifact artifact,
			final String artifactAbsolutePath ) throws IOException, MojoExecutionException
	{
		if ( artifact.getScope().equals( "provided" ) )
			return;

		final String jarName = "lib/" + artifact.getArtifactId() + "." + artifact.getType();
		final InputStream inputStream = new FileInputStream( artifactAbsolutePath );
		zip.add( jarName, inputStream );
		copyFilesFromJarToZip( zip, artifactAbsolutePath );
	}

	String getArtifactAbsolutePath( final Artifact artifact )
			throws ArtifactResolutionException, ArtifactNotFoundException
	{
		resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
		return artifact.getFile().getAbsolutePath();
	}

	void copyFinalArtifactToZip( final ZipFileWriter zip ) {
		try {
			final String fileName = String.format( "%s.%s", finalName, project.getPackaging() );
			final InputStream inputStream = new FileInputStream( new File( targetDirectory, fileName ) );
			zip.add( "lib/" + fileName, inputStream );
		} catch ( final FileNotFoundException cause ) {
			System.out.println( cause.getMessage() + ". Ignoring." );
		}
	}

	void copyWebResourceFolderToZip( final ZipFileWriter zip ) throws IOException
	{
		final File directory = new File( webresourcesPath );
		copyDirectoryFilesToZip( zip, directory, "webapp" );
	}

	void copyDirectoryFilesToZip( final ZipFileWriter zip, final File directory, final String rootDirectory ) throws IOException {
		if ( directory.exists() )
			for ( final File file : directory.listFiles() )
				copyToZip( zip, rootDirectory, file );
	}

	void copyToZip( final ZipFileWriter zip, final String rootDirectory, final File file )
			throws IOException, FileNotFoundException
	{
		final String fileName = ( rootDirectory + "/" + file.getName() ).replaceFirst( "^/", "" );
		if ( file.isDirectory() )
			copyDirectoryFilesToZip( zip, file, fileName );
		else
			copyFileToZip( zip, file, fileName );
	}

	void copyFileToZip( final ZipFileWriter zip, final File file, final String fileName )
			throws FileNotFoundException, IOException
	{
		@Cleanup final InputStream content = new FileInputStream( file );
		zip.add( fileName, content );
	}
}