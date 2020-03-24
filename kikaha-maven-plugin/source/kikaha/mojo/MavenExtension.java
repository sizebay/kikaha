package kikaha.mojo;

import lombok.val;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.DefaultArtifact;

public class MavenExtension {

    public static String getArtifactAbsolutePath(final MavenProject project, final Artifact artifact) {
        val aetherArtifact = new DefaultArtifact(
            artifact.getGroupId(),
            artifact.getArtifactId(),
            artifact.getClassifier(),
            artifact.getVersion()
        );

        val projectBuildingRequest = project.getProjectBuildingRequest();
        val localRepo = projectBuildingRequest.getRepositorySession().getLocalRepositoryManager();
        val rootPath = localRepo.getRepository().getBasedir().getAbsolutePath();
        return rootPath + "/" + localRepo.getPathForLocalArtifact(aetherArtifact) + "." + artifact.getType();
    }
}
