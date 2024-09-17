package kikaha.cloud.aws.iam;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.auth.*;
import kikaha.config.Config;
import kikaha.core.cdi.*;
import lombok.*;

/**
 * A producer of {@link AWSCredentials}.
 */
@Singleton
public class AmazonCredentialsProducer {

	@Inject CDI cdi;

	@Getter( lazy = true )
	private final Config config = cdi.load( Config.class );

	@Getter( lazy = true )
	private final AmazonCredentialsFactory factory = loadCredentialFactory();

	private AmazonCredentialsFactory loadCredentialFactory(){
		final Class<?> aClass = getConfig().getClass( "server.aws.credentials-factory" );
		return  (AmazonCredentialsFactory)cdi.load( aClass );
	}

	@Produces
	public AWSCredentials produceCredentials(){
		return getFactory().loadCredentialProvider().getCredentials();
	}

	public AWSCredentialsProvider getCredentialProvider() {
		return getFactory().loadCredentialProvider();
	}
}
