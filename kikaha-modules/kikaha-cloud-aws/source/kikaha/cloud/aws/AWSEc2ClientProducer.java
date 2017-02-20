package kikaha.cloud.aws;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.ec2.*;
import kikaha.config.Config;

/**
 * @author: miere.teixeira
 */
@Singleton
public class AWSEc2ClientProducer {

	@Inject Config config;
	@Inject ClientConfiguration configuration;
	@Inject AWSCredentialsProducer credentialsProducer;

	@Produces
	public AmazonEC2 produceAmazonEC2Client(){
		final String credentialName = config.getString( "server.aws.ec2.default-credential" );
		final AWSCredentialsProvider credentials = credentialsProducer.getCredentialProvider( credentialName );
		return AmazonEC2ClientBuilder.standard()
			.withClientConfiguration( configuration )
			.withCredentials( credentials )
			.build();
	}
}
