package kikaha.cloud.aws;

import javax.inject.*;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.*;
import kikaha.config.Config;
import lombok.Value;

/**
 *
 */
@Singleton
public class AmazonConfigurationProducer {

	@Inject Config config;
	@Inject
	AmazonCredentialsProducer credentialsProducer;

	public AmazonWebServiceConfiguration configForService(String serviceAlias ){
		final Config config = this.config.getConfig("server.aws." + serviceAlias);
		if ( config == null )
			throw new IllegalStateException( "No configuration for Amazon Web Service found with name '" + serviceAlias + "'" );

		final String regionName = config.getString("region", "us-west-2");
		final AWSCredentialsProvider credentialProvider = credentialsProducer.getCredentialProvider(config.getString("iam-policy", serviceAlias));
		return new AmazonWebServiceConfiguration(
			credentialProvider,
			Regions.fromName( regionName )
		);
	}

	@Value
	public static final class AmazonWebServiceConfiguration {
		final AWSCredentialsProvider iamPolicy;
		final Regions region;
	}
}
