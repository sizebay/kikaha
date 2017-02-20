package kikaha.cloud.aws;

import static org.junit.Assert.assertEquals;
import javax.inject.Inject;
import com.amazonaws.auth.*;
import com.amazonaws.services.ec2.AmazonEC2;
import kikaha.core.test.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for AWSEc2ClientProducer.
 */
@RunWith( KikahaRunner.class )
public class AWSEc2ClientProducerTest {

	@Inject AWSEc2ClientProducer producer;

	@Test
	public void ensureCanRetrieveTheEC2ClientWithTheRightCredential(){
		final AmazonEC2 client = producer.produceAmazonEC2Client();
		final Exposed exposed = new Exposed( client );
		final AWSCredentialsProvider credentialsProvider = exposed.getFieldValue( "awsCredentialsProvider", AWSCredentialsProvider.class );
		final AWSCredentials credentials = credentialsProvider.getCredentials();
		assertEquals( "1234", credentials.getAWSAccessKeyId() );
		assertEquals( "4321", credentials.getAWSSecretKey() );
	}
}