package kikaha.cloud.auth0;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import kikaha.config.Config;
import kikaha.core.modules.security.FormAuthenticationMechanism;
import lombok.*;

/**
 *
 */
@Singleton
public class Auth0 {

	@Inject Config config;
	@Inject FormAuthenticationMechanism formAuthenticationMechanism;

	@Produces
	AuthConfig produceAuthConfig(){
		return new AuthConfig(
			config.getString( "server.auth0.issuer" ),
			config.getString( "server.auth0.client-id" ),
			config.getString( "server.auth0.client-secret" ),
			config.getString( "server.auth0.signing-algorithm" ),
			config.getString( "server.auth0.public-key-path" ),
			config.getString( "server.auth.success-location" ),
			formAuthenticationMechanism.getErrorPage(),
			formAuthenticationMechanism.getLoginPage(),
			config.getBoolean( "server.auth0.base64-encoded-secret" )
		);
	}

	@Getter
	@RequiredArgsConstructor
	static public class AuthConfig {
		final String issuer;
		final String clientId;
		final String clientSecret;
		final String signingAlgorithm;
		final String publicKeyPath;
		final String redirectOnSuccess;
		final String redirectOnError;
		final String redirectOnAuthFailure;
		final boolean base64EncodedSecret;
	}
}
