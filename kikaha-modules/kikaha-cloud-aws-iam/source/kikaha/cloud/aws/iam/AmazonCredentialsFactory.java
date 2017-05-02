package kikaha.cloud.aws.iam;

import javax.inject.Inject;
import java.util.*;
import com.amazonaws.auth.*;
import kikaha.config.Config;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * A factory to load {@link AWSCredentials}. It is useful to customize the way credentials will be
 * loaded on the Kikaha container.<br>
 * <br>
 * See {@code http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html}
 */
public interface AmazonCredentialsFactory {

	/**
	 * Retrieve the {@link AWSCredentials} for a given {@code profileName}.
	 *
	 * @param profileName
	 * @return
	 */
	AWSCredentials loadCredentialFor( String profileName );

	/**
	 * Retrieve Credentials from the default Credential Provider Chain.
	 */
	class Default implements AmazonCredentialsFactory {

		final AWSCredentialsProviderChain chain = DefaultAWSCredentialsProviderChain.getInstance();

		@Override
		public AWSCredentials loadCredentialFor( String profileName ) {
			return chain.getCredentials();
		}
	}

	@Setter
	@Accessors( chain = true )
	class Yml implements AmazonCredentialsFactory, AWSCredentialsProvider {

		final Map<String, AWSCredentials> cache = new HashMap<>();

		@Inject Config config;

		@Override
		public AWSCredentials getCredentials() {
			return loadCredentialFor( "default" );
		}

		@Override
		public AWSCredentials loadCredentialFor( String profileName ) {
			return cache.computeIfAbsent( profileName, this::createCredential );
		}

		BasicAWSCredentials createCredential( String name ) {
			final Config config = this.config.getConfig("server.aws.iam-policies." + name);
			if ( config == null )
				throw new IllegalStateException( "No AWS Configuration available for '"+ name +"'" );
			return new BasicAWSCredentials(
					config.getString( "access-key-id" ),
					config.getString( "secret-access-key" )
			);
		}

		@Override
		public void refresh() {
			cache.clear();
		}
	}
}
