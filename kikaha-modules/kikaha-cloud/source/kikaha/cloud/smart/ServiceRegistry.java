package kikaha.cloud.smart;

import java.io.IOException;
import kikaha.core.modules.security.SessionIdGenerator;
import lombok.*;

/**
 * Defines how a smart-server should behave when it is deployed on a cloud environment.
 */
public interface ServiceRegistry {

	/**
	 * Generate an unique identifier to the current machine. Developers are encouraged to
	 * implement this method in order to provide a more reliable identifier.
	 *
	 * @throws IOException whenever the ServiceRegistry wasn't able to generate/retrieve the ID.
	 * @return the machine identifier
	 */
	default String generateTheMachineId() throws IOException {
		return SessionIdGenerator.MAC_ADDRESS;
	}

	/**
	 * Register the current application into a cluster.
	 *
	 * @param applicationData contains the basic needed information to join the cluster.
	 * @throws IOException whenever the ServiceRegistry wasn't able to register this application.
	 */
	void registerCluster( final ApplicationData applicationData ) throws IOException ;

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
