package kikaha.cloud.aws;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
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
	public AmazonEC2Client produceAmazonEC2Client(){
		final String credentialName = config.getString( "server.aws.ec2.default-credential" );
		final AWSCredentials credentials = credentialsProducer.getCredentials( credentialName );
		return new AmazonEC2Client( credentials, configuration );
	}
}
