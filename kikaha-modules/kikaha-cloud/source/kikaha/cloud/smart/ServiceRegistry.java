package kikaha.cloud.smart;

import java.io.IOException;
import java.util.*;

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
	 * Retrieves a list of FQDNS (or IP) locations representing all other nodes that
	 * have already joined to the cluster. Although there is no listener to
	 * be notified about application nodes that joining or leaving the cluster at real time,
	 * you can call this method whenever you need a fresh list of node nodes that is part of
	 * the same cluster this application.
	 *
	 * @return
	 * @throws IOException
     * @param applicationData
	 */
	List<String> locateSiblingNodesOnTheCluster(ApplicationData applicationData) throws IOException;

	/**
	 * Contains the basic information needed information to join a cluster.
	 */
	@RequiredArgsConstructor
	class ApplicationData {
		@NonNull final SupplierThatMayFail<String> machineId;
		@NonNull final SupplierThatMayFail<String> localAddress;
		@Getter @NonNull final String name;
		@Getter @NonNull final String version;
		@Getter final int localPort;
		@Getter final boolean isHttps;
		@NonNull ServiceRegistry serviceRegistry;

		public final String getMachineId() throws IOException {
			return machineId.get();
		}

		public final String getLocalAddress() throws IOException {
			return localAddress.get();
		}

		public final List<String> getSiblingNodesOnTheCluster() throws IOException {
			if ( serviceRegistry != null )
				return serviceRegistry.locateSiblingNodesOnTheCluster( this );
			return Collections.emptyList();
		}
	}

	interface SupplierThatMayFail<T> {
		T get() throws IOException;
	}
}
