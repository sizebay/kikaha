package kikaha.cloud.aws.ec2;

import static kikaha.core.test.Exposed.expose;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import javax.inject.Named;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.*;
import com.amazonaws.services.ec2.AmazonEC2;
import kikaha.cloud.aws.iam.*;
import kikaha.cloud.aws.iam.AmazonCredentialsFactory.Yml;
import kikaha.config.*;
import kikaha.core.cdi.CDI;
import kikaha.core.cdi.helpers.KeyValueProviderContext;
import kikaha.core.test.Exposed;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for AmazonEC2ClientProducer.
 */
@RunWith( MockitoJUnitRunner.class )
public class AmazonEC2ClientProducerTest {

	final Config defaultConfiguration = ConfigLoader.loadDefaults();
	final Yml yml = new Yml().setConfig( defaultConfiguration );

	@Mock CDI cdi;
	@Mock Named named;

	ClientConfiguration clientConfiguration = new ClientConfiguration();
	AmazonCredentialsProducer credentialsProducer = new AmazonCredentialsProducer();
	AmazonConfigurationProducer configurationProducer = new AmazonConfigurationProducer();
	AmazonEC2ClientProducer ec2ClientProducer = new AmazonEC2ClientProducer();

	@Before
	public void configureMocks(){
		doReturn( defaultConfiguration ).when( cdi ).load( eq(Config.class) );
		doReturn( yml ).when( cdi ).load( eq( Yml.class ) );

		expose( credentialsProducer ).setFieldValue( "cdi", cdi );

		expose( configurationProducer )
			.setFieldValue( "cdi", cdi )
			.setFieldValue( "credentialsProducer", credentialsProducer );

		doReturn( "ec2" ).when( named ).value();
		final KeyValueProviderContext providerContext = new KeyValueProviderContext();
		providerContext.setAnnotation( Named.class, named );

		ec2ClientProducer.clientConfiguration = clientConfiguration;
		ec2ClientProducer.configuration = configurationProducer.produceConfig( providerContext );
	}

	@Test
	public void ensureCanRetrieveTheEC2ClientWithTheRightCredential(){
		final AmazonEC2 client = ec2ClientProducer.produceAmazonEC2Client();
		final Exposed exposed = new Exposed( client );
		final AWSCredentialsProvider credentialsProvider = exposed.getFieldValue( "awsCredentialsProvider", AWSCredentialsProvider.class );
		final AWSCredentials credentials = credentialsProvider.getCredentials();
		assertEquals( "1234", credentials.getAWSAccessKeyId() );
		assertEquals( "4321", credentials.getAWSSecretKey() );
	}
}