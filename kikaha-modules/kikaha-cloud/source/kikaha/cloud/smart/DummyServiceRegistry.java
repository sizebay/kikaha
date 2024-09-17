package kikaha.cloud.smart;

import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class DummyServiceRegistry implements ServiceRegistry {

	@Override
	public void registerIntoCluster(ApplicationData applicationData) throws IOException {
		log.info( "No ServiceRegistry defined." );
	}

	@Override
	public void deregisterFromCluster(ApplicationData applicationData) throws IOException {
		log.info( "No cluster to leave." );
	}

	@Override
	public List<ApplicationData> locateSiblingNodesOnTheCluster(ApplicationData applicationData) throws IOException {
		return null;
	}
}
