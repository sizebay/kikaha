package kikaha.cloud.aws.iam;

import static org.junit.Assert.*;
import javax.inject.Inject;
import com.amazonaws.auth.*;
import com.amazonaws.regions.*;
import kikaha.cloud.aws.iam.AmazonConfigurationProducer.AmazonWebServiceConfiguration;
import kikaha.config.ConfigLoader;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class AmazonConfigurationProducerTest {

	@Inject AmazonConfigurationProducer producer;

	@Test
	public void ensureThatCanRetrieveConfigurationForHypotheticalEC2Service(){
		final BasicAWSCredentials credentials = new BasicAWSCredentials("2345", "5432");
		final AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
		final AmazonWebServiceConfiguration expected = new AmazonWebServiceConfiguration( credentialsProvider, Regions.SA_EAST_1, ConfigLoader.loadDefaults());
		final AmazonWebServiceConfiguration configuration = producer.configForService("hypothetical-ec2");
		assertNotNull( configuration );

		final AWSCredentials actualCredentials = configuration.getIamPolicy().getCredentials();
		assertEquals( credentials.getAWSAccessKeyId(), actualCredentials.getAWSAccessKeyId() );
		assertEquals( credentials.getAWSSecretKey(), actualCredentials.getAWSSecretKey() );
		assertEquals( expected.getRegion(), configuration.getRegion() );
	}
}