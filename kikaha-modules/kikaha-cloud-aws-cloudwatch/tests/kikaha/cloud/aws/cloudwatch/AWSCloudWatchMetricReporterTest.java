package kikaha.cloud.aws.cloudwatch;

import static org.junit.Assert.assertNotNull;
import javax.inject.Inject;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class AWSCloudWatchMetricReporterTest {

	@Inject AWSCloudWatchMetricReporter reporter;

	@Test
	public void ensureConfigurationWasProperlyInjected(){
		assertNotNull( reporter.configuration );
	}
}