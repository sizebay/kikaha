package kikaha.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import kikaha.cloud.smart.ServiceRegistry;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.config.Config;
import kikaha.core.cdi.Application;
import kikaha.core.test.KikahaRunner;
import lombok.Cleanup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by miere.teixeira on 16/06/2017.
 */
@RunWith(KikahaRunner.class)
public class HazelcastProgrammaticConfigurationIntegrationTest {

    @Inject HazelcastConfigurationProducer configurationProducer;
    @Inject Config originalConfig;
    @Inject ApplicationData originalApplicationData;

    ApplicationData applicationData;
    Config config;

    @Before
    public void configMocks() throws IOException {
        config = mock( Config.class );
        applicationData = mock( ApplicationData.class );

        doReturn( asList( ApplicationData.forTest( "127.0.0.1" ) ) ).when( applicationData ).getSiblingNodesOnTheCluster();
        doReturn( "application" ).when( applicationData ).getCanonicalName();
        doReturn( null ).when( config ).getString( eq("server.hazelcast.config") );

        configurationProducer.config = config;
        configurationProducer.applicationData = applicationData;
    }

    @Test
    public void twoMembersAreAbleToJoinTheCluster() throws InterruptedException {
        doReturn( true ).when( config ).getBoolean( eq("server.hazelcast.connect-to-cluster-members") );
        doReturn( false ).when( config ).getBoolean( eq("server.hazelcast.enable-multicast") );

        final com.hazelcast.config.Config hazelcastConfig = configurationProducer.getHazelcastConfig();
        @Cleanup( "shutdown" ) final HazelcastInstance hazelcastInstance0 = Hazelcast.newHazelcastInstance(hazelcastConfig);
        @Cleanup( "shutdown" ) final HazelcastInstance hazelcastInstance1 = Hazelcast.newHazelcastInstance(hazelcastConfig);

        final IMap<Object, Object> map0 = hazelcastInstance0.getMap("map");
        final IMap<Object, Object> map1 = hazelcastInstance1.getMap("map");

        map0.put("hello", "world");
        Thread.sleep(200);
        assertNotNull(map1.get("hello"));
    }
}
