package kikaha.cloud.aws.iam;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.auth.*;
import kikaha.config.Config;
import kikaha.core.cdi.*;
import lombok.NonNull;

/**
 * A producer of {@link AWSCredentials}.
 */
@Singleton
public class AmazonCredentialsProducer {

	@Inject Config config;
	@Inject CDI cdi;
	AmazonCredentialsFactory factory;

	@PostConstruct
	public void loadCredentialFactory(){
		final Class<?> aClass = config.getClass( "server.aws.credentials-factory" );
		factory = (AmazonCredentialsFactory)cdi.load( aClass );
	}

	@Produces
	public AWSCredentials produceCredentials( final ProviderContext context ){
		final IAM annotation = context.getAnnotation(IAM.class);
		final String configurationName = annotation != null ? annotation.value() : "default";
		return getCredentials( configurationName );
	}

	public AWSCredentialsProvider getCredentialProvider( @NonNull final String configurationName  ) {
		final AWSCredentials credentials = getCredentials( configurationName );
		return new AWSStaticCredentialsProvider( credentials );
	}

	private AWSCredentials getCredentials( String profileName ) {
		return factory.loadCredentialFor( profileName );
	}

}
