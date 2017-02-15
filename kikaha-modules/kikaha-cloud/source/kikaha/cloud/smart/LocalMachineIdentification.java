package kikaha.cloud.smart;

import java.io.IOException;
import kikaha.core.modules.security.SessionIdGenerator;

/**
 * Represents a local machine identification. It contains all information needed to
 * identify the current machine on a cloud environment. Implementations of this interface
 * may request external services - such as AWS SDK or an external service coordinator - to
 * retrieve the required information. Thus, use this carefully in order to avoid network
 * overhead or latency-related issues.
 */
public interface LocalMachineIdentification {

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
	 * @throws IOException whenever the ServiceRegistry wasn't able to retrieve the local address.
	 * @return the IP/FQDNS address the current machine belongs
	 */
	String getLocalAddress() throws IOException;
}
