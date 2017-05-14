package kikaha.cloud.metrics;

import static org.junit.Assert.assertTrue;
import com.codahale.metrics.Timer;
import org.junit.Test;

/**
 *
 */
public class AllowEveryThingTest {

	@Test
	public void matchesWillAlwaysReturnTrue() throws Exception {
		final Timer timer = new Timer();
		assertTrue( new DefaultRegistryConfiguration.AllowEveryThing().matches( "any", timer ) );
	}
}