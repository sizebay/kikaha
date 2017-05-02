package kikaha.cloud.aws.alb;

import static kikaha.core.test.Exposed.expose;
import static org.mockito.Mockito.*;
import java.io.IOException;
import com.amazonaws.*;
import com.amazonaws.http.SdkHttpMetadata;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancingv2.model.*;
import kikaha.cloud.aws.ec2.AmazonEC2ClientProducer;
import kikaha.cloud.aws.iam.*;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.config.*;
import kikaha.core.cdi.CDI;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link AmazonELBServiceRegistry}.
 */
@RunWith( MockitoJUnitRunner.class )
public class AmazonELBServiceRegistryTest {

	final ApplicationData applicationData = new ApplicationData( () -> "unit01", () -> "localhost", "unit01", "1.0", 9000, false);
	final Config defaultConfiguration = ConfigLoader.loadDefaults();
	final AmazonCredentialsFactory.Yml yml = new AmazonCredentialsFactory.Yml().setConfig( defaultConfiguration );

	@Mock CDI cdi;
	@Mock Config config;
	@Mock AmazonElasticLoadBalancing client;
	@Mock RegisterTargetsResult registerTargetsResult;
	@Mock DeregisterTargetsResult deregisterTargetsResult;
	@Mock ResponseMetadata sdkResponseMetadata;
	@Mock SdkHttpMetadata sdkHttpMetadata;

	AmazonELBServiceRegistry registry = new AmazonELBServiceRegistry();
	ClientConfiguration clientConfiguration = new ClientConfiguration();
	AmazonCredentialsProducer credentialsProducer = new AmazonCredentialsProducer();
	AmazonConfigurationProducer configurationProducer = new AmazonConfigurationProducer();
	AmazonEC2ClientProducer ec2ClientProducer = new AmazonEC2ClientProducer();

	@Before
	public void configureMocks(){
		doReturn( defaultConfiguration ).when( cdi ).load( eq(Config.class) );
		doReturn( yml ).when( cdi ).load( eq( AmazonCredentialsFactory.Yml.class ) );

		expose( credentialsProducer ).setFieldValue( "cdi", cdi );

		expose( configurationProducer )
			.setFieldValue( "cdi", cdi )
			.setFieldValue( "credentialsProducer", credentialsProducer );

		expose( ec2ClientProducer )
			.setFieldValue("configurationProducer", configurationProducer)
			.setFieldValue("configuration", clientConfiguration);


		doReturn( client ).when( registry ).elbClient();
		doReturn( registerTargetsResult ).when(client).registerTargets( any() );
		doReturn( deregisterTargetsResult ).when(client).deregisterTargets( any() );
		doReturn( sdkResponseMetadata ).when(registerTargetsResult).getSdkResponseMetadata();
		doReturn( sdkResponseMetadata ).when(deregisterTargetsResult).getSdkResponseMetadata();
		doReturn( sdkHttpMetadata ).when(registerTargetsResult).getSdkHttpMetadata();
		doReturn( sdkHttpMetadata ).when(deregisterTargetsResult).getSdkHttpMetadata();
	}

	@Before
	public void setupMocks(){
		registry.config = config;
		registry = Mockito.spy( registry );
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