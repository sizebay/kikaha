package kikaha.cloud.aws.alb;

import static kikaha.core.test.Exposed.expose;
import static org.mockito.Mockito.*;
import java.io.IOException;
import javax.inject.Named;
import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.http.SdkHttpMetadata;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancingv2.model.*;
import kikaha.cloud.aws.ec2.AmazonEC2ClientProducer;
import kikaha.cloud.aws.iam.*;
import kikaha.cloud.aws.iam.AmazonConfigurationProducer.AmazonWebServiceConfiguration;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.config.Config;
import kikaha.core.cdi.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link AmazonELBServiceRegistry}.
 */
@RunWith( MockitoJUnitRunner.class )
public class AmazonELBServiceRegistryTest {

	final ApplicationData applicationData = new ApplicationData( () -> "unit01", () -> "localhost", "unit01", "1.0", 9000, false, null);

	@Mock CDI cdi;
	@Mock Config config;
	@Mock Config elbConfig;
	@Mock AmazonElasticLoadBalancing client;
	@Mock RegisterTargetsResult registerTargetsResult;
	@Mock DeregisterTargetsResult deregisterTargetsResult;
	@Mock ResponseMetadata sdkResponseMetadata;
	@Mock SdkHttpMetadata sdkHttpMetadata;
	@Mock ProviderContext providerContext;
	@Mock AmazonCredentialsFactory amazonCredentialsFactory;
	@Mock AWSCredentialsProvider awsCredentialsProvider;
	@Mock AWSCredentials awsCredentials;
	@Mock Named named;

	AmazonWebServiceConfiguration amazonWebServiceConfiguration;

	AmazonELBServiceRegistry registry = new AmazonELBServiceRegistry();
	ClientConfiguration clientConfiguration = new ClientConfiguration();
	AmazonCredentialsProducer credentialsProducer = new AmazonCredentialsProducer();
	AmazonConfigurationProducer configurationProducer = new AmazonConfigurationProducer();
	AmazonEC2ClientProducer ec2ClientProducer = new AmazonEC2ClientProducer();

	@Before
	//FIXME: worst mock setup ever! Use integration test instead.
	public void configureMocks(){
		doReturn( elbConfig ).when( config ).getConfig( "server.aws.elb" );
		doAnswer( a -> a.getArgumentAt(1, String.class) ).when( elbConfig ).getString( anyString(), anyString() );
		doReturn( config ).when( cdi ).load( eq(Config.class) );
		doReturn( "us-east-1" ).when( config ).getString( eq("server.aws.default.region") );
		doReturn( "target-group" ).when( elbConfig ).getString( eq("target-group") );
		doReturn( AmazonCredentialsFactory.class ).when( config ).getClass( eq("server.aws.credentials-factory") );
		doReturn( amazonCredentialsFactory ).when( cdi ).load( eq( AmazonCredentialsFactory.class ) );
		doReturn( awsCredentialsProvider ).when( amazonCredentialsFactory ).loadCredentialProvider();
		doReturn( awsCredentials ).when( awsCredentialsProvider ).getCredentials();

		doReturn( "elb" ).when( named ).value();
		doReturn( named ).when( providerContext ).getAnnotation( eq(Named.class) );

		expose( credentialsProducer ).setFieldValue( "cdi", cdi );

		expose( configurationProducer )
			.setFieldValue( "cdi", cdi )
			.setFieldValue( "credentialsProducer", credentialsProducer );

		amazonWebServiceConfiguration = configurationProducer.produceConfig( providerContext );

		expose( ec2ClientProducer )
			.setFieldValue("configuration", amazonWebServiceConfiguration )
			.setFieldValue("clientConfiguration", clientConfiguration);

		doReturn( registerTargetsResult ).when(client).registerTargets( any() );
		doReturn( deregisterTargetsResult ).when(client).deregisterTargets( any() );
		doReturn( sdkResponseMetadata ).when(registerTargetsResult).getSdkResponseMetadata();
		doReturn( sdkResponseMetadata ).when(deregisterTargetsResult).getSdkResponseMetadata();
		doReturn( sdkHttpMetadata ).when(registerTargetsResult).getSdkHttpMetadata();
		doReturn( sdkHttpMetadata ).when(deregisterTargetsResult).getSdkHttpMetadata();

		registry.amazonWebServiceConfiguration = amazonWebServiceConfiguration;
		registry.elasticLoadBalancing = client;
		registry = Mockito.spy( registry );
	}

	@Test( expected = IOException.class )
	public void ensureThatCannotJoinELBWhenNoTargetGroupIsDefined() throws IOException {
		registry.registerIntoCluster( applicationData );
	}

	@Test
	public void ensureThatCanJoinELBWhenTheTargetGroupIsDefined() throws IOException {
		doReturn( "targetGroup" ).when( elbConfig ).getString( "target-group" );
		doReturn( 200 ).when( sdkHttpMetadata ).getHttpStatusCode();
		registry.registerIntoCluster( applicationData );
		verify( client ).registerTargets( any() );
	}

	@Test( expected = IOException.class )
	public void ensureThatCannotLeaveELBWhenNoTargetGroupIsDefined() throws IOException {
		doReturn( "targetGroup" ).when( elbConfig ).getString( "target-group" );
		registry.deregisterFromCluster( applicationData );
	}

	@Test
	public void ensureThatCanLeaveELBWhenTheTargetGroupIsDefined() throws IOException {
		doReturn( "targetGroup" ).when( elbConfig ).getString( "target-group" );
		doReturn( 200 ).when( sdkHttpMetadata ).getHttpStatusCode();
		registry.deregisterFromCluster( applicationData );
		verify( client ).deregisterTargets( any() );
	}
}