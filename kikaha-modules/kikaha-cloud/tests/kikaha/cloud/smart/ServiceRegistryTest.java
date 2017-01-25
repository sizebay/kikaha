package kikaha.cloud.smart;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import kikaha.core.modules.security.SessionIdGenerator;
import org.junit.Test;

/**
 * Unit tests for ServiceRegistry.
 */
public class ServiceRegistryTest {

	final ServiceRegistry registry = new StubServiceRegistry();

	@Test
	public void ensureTheDefaultGeneratedMachineIdIsTheMacAddress() throws Exception {
		final String machineId = registry.generateTheMachineId();
		assertEquals( SessionIdGenerator.MAC_ADDRESS, machineId );
	}

}

class StubServiceRegistry implements ServiceRegistry {

	@Override
	public void registerIntoCluster(ApplicationData applicationData) throws IOException {
		throw new UnsupportedOperationException("registerIntoCluster not implemented yet!");
	}

	@Override
	public void deregisterFromCluster(ApplicationData applicationData) throws IOException {
		throw new UnsupportedOperationException("deregisterFromCluster not implemented yet!");
	}
}