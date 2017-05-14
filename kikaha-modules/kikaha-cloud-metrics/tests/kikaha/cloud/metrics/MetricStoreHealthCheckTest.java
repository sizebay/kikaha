package kikaha.cloud.metrics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link MetricStoreHealthCheck}.
 */
public class MetricStoreHealthCheckTest {

	@Test
	public void ensureInitialStateIsHealthy(){
		final MetricStoreHealthCheck healthCheck = new MetricStoreHealthCheck();
		assertTrue( healthCheck.execute().isHealthy() );
	}

	@Test
	public void ensureCanRegisterExceptionAsUnhealthy() throws Exception {
		final MetricStoreHealthCheck healthCheck = new MetricStoreHealthCheck();
		healthCheck.setUnhealthy( new NullPointerException() );
		assertFalse( healthCheck.execute().isHealthy() );
	}

	@Test
	public void ensureCanTurnHealthyAgain() throws Exception {
		final MetricStoreHealthCheck healthCheck = new MetricStoreHealthCheck();
		healthCheck.setUnhealthy( new NullPointerException() );
		assertFalse( healthCheck.execute().isHealthy() );
		healthCheck.setHealthy();
		assertTrue( healthCheck.execute().isHealthy() );
	}
}