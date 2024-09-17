package kikaha.cloud.aws.alb;

import static org.junit.Assert.assertNotNull;
import java.util.List;
import javax.inject.Inject;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.core.test.KikahaRunner;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 * Integration Test for {@link AmazonELBServiceRegistry}.
 */
@RunWith(KikahaRunner.class)
public class AmazonELBServiceRegistryITest {

	@Inject AmazonELBServiceRegistry registry;
	@Inject ApplicationData applicationData;

	@Test
	@Ignore
	public void canLocateSiblingNodesOnTheCluster() throws Exception {
		final List<ApplicationData> foundSiblingNodes = registry.locateSiblingNodesOnTheCluster(applicationData);
		assertNotNull( foundSiblingNodes );
		System.out.println("foundSiblingNodes = " + foundSiblingNodes);
	}
}