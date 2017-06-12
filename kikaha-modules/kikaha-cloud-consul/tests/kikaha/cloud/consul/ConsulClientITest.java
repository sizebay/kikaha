package kikaha.cloud.consul;

import kikaha.cloud.smart.ServiceRegistry;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.core.test.KikahaRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import static org.junit.Assert.assertNotNull;

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
        final List<String> foundAddresses = consulClient.locateSiblingNodesOnTheCluster(applicationData);
        assertNotNull( foundAddresses );
        consulClient.deregisterFromCluster( applicationData );
    }
}
