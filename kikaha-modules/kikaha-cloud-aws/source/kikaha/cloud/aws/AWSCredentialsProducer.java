package kikaha.cloud.aws;

import java.util.*;
import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.auth.*;
import kikaha.config.Config;
import kikaha.core.cdi.ProviderContext;

/**
 * A producer of {@link AWSCredentials}.
 */
@Singleton
public class AWSCredentialsProducer {

	final Map<String, AWSCredentials> cache = new HashMap<>();

	@Inject Config config;

	@Produces
	public AWSCredentials produceCredentials(ProviderContext context ){
		final IAM annotation = context.getAnnotation(IAM.class);
		final String configurationName = annotation != null ? annotation.value() : "default";
		return cache.computeIfAbsent( configurationName, this::createCredential );
	}

	BasicAWSCredentials createCredential( String name ) {
		final Config config = this.config.getConfig("server.aws.credentials." + name);
		if ( config == null )
			throw new IllegalStateException( "No AWS Configuration available for '"+ name +"'" );
		return new BasicAWSCredentials(
			config.getString( "access-key-id" ),
			config.getString( "secret-access-key" )
		);
	}
}
