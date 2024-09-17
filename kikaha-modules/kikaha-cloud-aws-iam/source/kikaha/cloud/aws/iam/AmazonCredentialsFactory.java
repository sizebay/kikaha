package kikaha.cloud.aws.iam;

import javax.inject.Inject;
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
	 * @return
	 */
	AWSCredentialsProvider loadCredentialProvider();

	/**
	 * Retrieve Credentials from the default Credential Provider Chain.
	 */
	class Default implements AmazonCredentialsFactory {

		final AWSCredentialsProviderChain chain = DefaultAWSCredentialsProviderChain.getInstance();

		@Override
		public AWSCredentialsProvider loadCredentialProvider() {
			return chain;
		}
	}

	@Setter
	@Accessors( chain = true )
	class Yml implements AmazonCredentialsFactory, AWSCredentialsProvider {

		//FIXME: it doesn't really need to be thread-safe, does it?
		volatile BasicAWSCredentials lastCredential;

		@Inject Config config;

		@Override
		public BasicAWSCredentials getCredentials() {
			if ( lastCredential == null )
				synchronized ( this ) {
					if ( lastCredential == null )
						lastCredential = loadCredentialFromConfig();
				}
			return lastCredential;
		}

		BasicAWSCredentials loadCredentialFromConfig(){
			final Config config = this.config.getConfig("server.aws.iam-policy");
			if ( config == null )
				throw new IllegalStateException( "No IAM Policy found" );
			return new BasicAWSCredentials(
					config.getString( "access-key-id" ),
					config.getString( "secret-access-key" )
			);
		}

		@Override
		public AWSCredentialsProvider loadCredentialProvider() {
			return this;
		}

		@Override
		public void refresh() {
			lastCredential = null;
		}
	}
}
