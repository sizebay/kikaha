package kikaha.cloud.aws.iam;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import kikaha.config.Config;
import kikaha.core.cdi.*;
import lombok.*;

/**
 *
 */
@Singleton
public class AmazonConfigurationProducer {

	@Inject CDI cdi;

	@Getter( lazy = true )
	private final Config config = cdi.load( Config.class );

	@Getter( lazy = true )
	private final String defaultRegion = getConfig().getString( "server.aws.default.region" );

	@Inject AmazonCredentialsProducer credentialsProducer;

	@Produces public AmazonWebServiceConfiguration produceConfig(ProviderContext context){
		final Named annotation = context.getAnnotation(Named.class);
		if ( annotation == null )
			throw new UnsupportedOperationException( "You must inform which configuration should be used. Use @Named('service')." );
		final String name = annotation.value();
		return configForService( name );
	}

	public AmazonWebServiceConfiguration configForService( String serviceAlias ){
		final Config config = getConfig().getConfig("server.aws." + serviceAlias);
		if ( config == null )
			throw new IllegalStateException( "No configuration for Amazon Web Service found with name '" + serviceAlias + "'" );

		final String regionName = config.getString("region", getDefaultRegion() );
		final AWSCredentialsProvider credentialProvider = credentialsProducer.getCredentialProvider();
		return new AmazonWebServiceConfiguration(
			credentialProvider,
			Regions.fromName( regionName ), config
		);
	}

	@Value
	public static class AmazonWebServiceConfiguration {
		final AWSCredentialsProvider iamPolicy;
		final Regions region;
		@lombok.experimental.Delegate
		final Config config;
	}
}
