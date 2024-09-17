package kikaha.cloud.consul;

import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.core.test.KikahaRunner;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 * Created by miere.teixeira on 12/06/2017.
 */
@RunWith(KikahaRunner.class)
public class ConsulClientITest {

    @Inject ConsulClient consulClient;
    @Inject ApplicationData applicationData;

    @Test
    @Ignore
    public void ensureCanJoinTheClusterDefinedOnTheConfigurationFile() throws IOException, InterruptedException {
        consulClient.registerIntoCluster( applicationData );
        final List<ApplicationData> foundAddresses = consulClient.locateSiblingNodesOnTheCluster(applicationData);
        assertNotNull( foundAddresses );
        consulClient.deregisterFromCluster( applicationData );
    }
}
