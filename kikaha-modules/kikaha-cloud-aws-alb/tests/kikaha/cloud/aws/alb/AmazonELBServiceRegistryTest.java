package kikaha.cloud.aws.alb;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import java.io.IOException;
import javax.inject.Inject;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.http.SdkHttpMetadata;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancingv2.model.*;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.config.Config;
import kikaha.core.test.KikahaRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 * Unit tests for {@link AmazonELBServiceRegistry}.
 */
@RunWith( KikahaRunner.class )
public class AmazonELBServiceRegistryTest {

	final ApplicationData applicationData = new ApplicationData( () -> "unit01", () -> "localhost", "unit01", "1.0", 9000, false);

	@Inject AmazonELBServiceRegistry registry;
	@Mock Config config;
	@Mock AmazonElasticLoadBalancing client;
	@Mock RegisterTargetsResult registerTargetsResult;
	@Mock DeregisterTargetsResult deregisterTargetsResult;
	@Mock ResponseMetadata sdkResponseMetadata;
	@Mock SdkHttpMetadata sdkHttpMetadata;

	@Before
	public void setupMocks(){
		initMocks(this);
		registry.config = config;
		registry = Mockito.spy( registry );

		doReturn( client ).when( registry ).elbClient();
		doReturn( registerTargetsResult ).when(client).registerTargets( any() );
		doReturn( deregisterTargetsResult ).when(client).deregisterTargets( any() );
		doReturn( sdkResponseMetadata ).when(registerTargetsResult).getSdkResponseMetadata();
		doReturn( sdkResponseMetadata ).when(deregisterTargetsResult).getSdkResponseMetadata();
		doReturn( sdkHttpMetadata ).when(registerTargetsResult).getSdkHttpMetadata();
		doReturn( sdkHttpMetadata ).when(deregisterTargetsResult).getSdkHttpMetadata();
	}

	@Test( expected = IOException.class )
	public void ensureThatCannotJoinELBWhenNoTargetGroupIsDefined() throws IOException {
		doReturn( null ).when( config ).getString( "server.aws.elb.target-group" );
		registry.registerIntoCluster( applicationData );
	}

	@Test
	public void ensureThatCanJoinELBWhenTheTargetGroupIsDefined() throws IOException {
		doReturn( "targetGroup" ).when( config ).getString( "server.aws.elb.target-group" );
		doReturn( 200 ).when( sdkHttpMetadata ).getHttpStatusCode();
		registry.registerIntoCluster( applicationData );
		verify( client ).registerTargets( any() );
	}

	@Test( expected = IOException.class )
	public void ensureThatCannotLeaveELBWhenNoTargetGroupIsDefined() throws IOException {
		doReturn( null ).when( config ).getString( "server.aws.elb.target-group" );
		registry.deregisterFromCluster( applicationData );
	}

	@Test
	public void ensureThatCanLeaveELBWhenTheTargetGroupIsDefined() throws IOException {
		doReturn( "targetGroup" ).when( config ).getString( "server.aws.elb.target-group" );
		doReturn( 200 ).when( sdkHttpMetadata ).getHttpStatusCode();
		registry.deregisterFromCluster( applicationData );
		verify( client ).deregisterTargets( any() );
	}
}