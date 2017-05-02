package kikaha.cloud.aws.iam;

import javax.inject.*;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.*;
import kikaha.config.Config;
import kikaha.core.cdi.CDI;
import lombok.*;

/**
 *
 */
@Singleton
public class AmazonConfigurationProducer {

	@Inject CDI cdi;

	@Getter( lazy = true )
	private final Config config = cdi.load( Config.class );

	@Inject AmazonCredentialsProducer credentialsProducer;

	public AmazonWebServiceConfiguration configForService(String serviceAlias ){
		final Config config = getConfig().getConfig("server.aws." + serviceAlias);
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
	public static class AmazonWebServiceConfiguration {
		final AWSCredentialsProvider iamPolicy;
		final Regions region;
	}
}
