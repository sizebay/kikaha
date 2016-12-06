package kikaha.mojo;

import java.io.*;
import java.util.*;
import kikaha.mojo.packager.JarWriter;
import lombok.Cleanup;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

@Mojo(name = "jar",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class KikahaJarPackagerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true)
    MavenProject project;

    @Parameter(defaultValue = "${project.basedir}/src/main/webapp", required = true)
    String webResourcesPath;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    File targetDirectory;

    @Parameter(defaultValue = "${project.build.finalName}", required = true)
    String finalName;

    @Component
    ArtifactResolver resolver;

    @Parameter(defaultValue = "${localRepository}", required = true)
    ArtifactRepository localRepository;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ensureTargetDirectoryExists();
        final JarWriter zip = createZipFile();
        getLog().info("Building application package: " + zip.getFileName());
        populateZip(zip);
        zip.flush();
    }

    private void ensureTargetDirectoryExists() throws MojoFailureException {
        if (!targetDirectory.exists())
            if (!targetDirectory.mkdirs())
                throw new MojoFailureException("Target Directory not created");
    }

    private JarWriter createZipFile() throws MojoExecutionException {
        try {
            final String fileName = targetDirectory.getAbsolutePath() + File.separatorChar + finalName + "-runnable.jar";
            final JarWriter zipFile = new JarWriter(fileName);
            return zipFile;
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    private void populateZip(final JarWriter zip) throws MojoExecutionException {
        try {
            copyDependenciesToZip(zip);
            copyFinalArtifactToZip(zip);
            copyWebResourceFolderToZip(zip);
        } catch (final IOException | ArtifactResolutionException | ArtifactNotFoundException e) {
            throw new MojoExecutionException("Failed to populate zip", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void copyDependenciesToZip(final JarWriter zip)
            throws ArtifactResolutionException, ArtifactNotFoundException, MojoExecutionException, IOException {
        final Set<String> namesAlreadyIncludedToZip = new HashSet<>();
        for (final Artifact artifact : (Set<Artifact>) project.getArtifacts()) {
            final String artifactAbsolutePath = getArtifactAbsolutePath(artifact);
            if (!namesAlreadyIncludedToZip.contains(artifactAbsolutePath)) {
                copyDependencyToZip(zip, artifact, artifactAbsolutePath);
                namesAlreadyIncludedToZip.add(artifactAbsolutePath);
            }
        }
    }

    private void copyDependencyToZip( final JarWriter zip, final Artifact artifact, final String artifactAbsolutePath )
        throws IOException, MojoExecutionException
    {
        if ( !artifact.getScope().equals("provided"))
            zip.mergeJar( artifactAbsolutePath );
    }

    private String getArtifactAbsolutePath(final Artifact artifact)
            throws ArtifactResolutionException, ArtifactNotFoundException {
        resolver.resolve(artifact, Collections.EMPTY_LIST, localRepository);
        return artifact.getFile().getAbsolutePath();
    }

    private void copyFinalArtifactToZip(final JarWriter zip) {
        try {
            final String fileName = String.format("%s.%s", finalName, project.getPackaging());
            final File file = new File(targetDirectory, fileName);
            zip.mergeJar( file.getAbsolutePath() );
        } catch (MojoExecutionException e) {
            e.printStackTrace();
            System.out.println(e.getMessage() + ". Ignoring.");
        }
    }

    private void copyWebResourceFolderToZip(final JarWriter zip) throws IOException {
        final File directory = new File(webResourcesPath);
        copyDirectoryFilesToZip(zip, directory, "webapp");
    }

    private void copyDirectoryFilesToZip(final JarWriter zip, final File directory, final String rootDirectory) throws IOException {
        if (directory.exists())
            for (final File file : directory.listFiles())
                copyToZip(zip, rootDirectory, file);
    }

    private void copyToZip(final JarWriter zip, final String rootDirectory, final File file) throws IOException {
        final String fileName = (rootDirectory + "/" + file.getName()).replaceFirst("^/", "");
        if (file.isDirectory())
            copyDirectoryFilesToZip(zip, file, fileName);
        else
            copyFileToZip(zip, file, fileName);
    }

    private void copyFileToZip(final JarWriter zip, final File file, final String fileName) throws IOException {
        @Cleanup final InputStream content = new FileInputStream(file);
        zip.add(fileName, content);
    }
}