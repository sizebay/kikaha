package kikaha.cloud.smart;

import java.io.IOException;
import kikaha.core.modules.security.SessionIdGenerator;
import lombok.*;

/**
 * Defines how a smart-server should behave when it is deployed on a cloud environment.
 */
public interface ServiceRegistry {

	/**
	 * Register the current application into a cluster.
	 *
	 * @param applicationData contains the basic needed information to join the cluster.
	 * @throws IOException whenever the ServiceRegistry wasn't able to register this application.
	 */
	void registerIntoCluster(final ApplicationData applicationData ) throws IOException;

	/**
	 * Leave the cluster.
	 *
	 * @param applicationData contains the basic needed information to leave the cluster.
	 * @throws IOException whenever the ServiceRegistry wasn't able to deregister this application.
	 */
	void deregisterFromCluster( final ApplicationData applicationData ) throws IOException;

	/**
	 * Contains the basic information needed information to join a cluster.
	 */
	@Getter
	@RequiredArgsConstructor
	class ApplicationData {
		@NonNull final String machineId;
		@NonNull final String name;
		@NonNull final String version;
		@NonNull final String localAddress;
		final int localPort;
		final boolean isHttps;
	}
}
