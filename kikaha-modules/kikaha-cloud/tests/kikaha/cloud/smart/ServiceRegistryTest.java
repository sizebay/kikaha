package kikaha.cloud.smart;

import static org.junit.Assert.assertEquals;

import kikaha.core.modules.security.SessionIdGenerator;
import org.junit.Test;

/**
 * Unit tests for ServiceRegistry.
 */
public class ServiceRegistryTest {

	final ServiceRegistry registry = a -> {};

	@Test
	public void ensureTheDefaultGeneratedMachineIdIsTheMacAddress() throws Exception {
		final String machineId = registry.generateTheMachineId();
		assertEquals( SessionIdGenerator.MAC_ADDRESS, machineId );
	}

}