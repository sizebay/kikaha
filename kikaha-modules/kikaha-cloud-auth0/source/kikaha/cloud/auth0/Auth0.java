package kikaha.cloud.auth0;

import static com.auth0.jwt.pem.PemReader.readPublicKey;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.*;
import java.security.*;
import java.util.*;
import com.auth0.*;
import com.auth0.jwt.JWTVerifier;
import io.undertow.security.idm.Account;
import kikaha.config.Config;
import kikaha.core.modules.security.*;
import lombok.*;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Singleton
public class Auth0 {

	static final String STATE = "state", TOKEN = "token", CODE = "code", HTTPS = "https://";

	@Inject Config config;

	@Getter(lazy = true)
	private final AuthConfig authConfig = new AuthConfig(
		config.getString( "server.auth.auth0.client-id" ),
		config.getString( "server.auth.auth0.client-secret" ),
		config.getString( "server.auth.auth0.client-domain" ),
		config.getString( "server.auth.auth0.signing-algorithm" ),
		config.getString( "server.auth.auth0.public-key-path" ),
		config.getBoolean( "server.auth.auth0.base64-encoded-secret" )
	);

	@Getter(lazy = true)
	private final Auth0Client auth0Client = new Auth0ClientImpl(
		getAuthConfig().clientId, getAuthConfig().clientSecret, getAuthConfig().clientDomain );

	@Getter(lazy = true)
	private final JWTVerifier verifier = getAuthConfig().loadVerifier();

	@Produces
	AuthConfig produceAuthConfig(){
		return getAuthConfig();
	}

	@Produces
	Auth0Client produceClient(){
		return getAuth0Client();
	}

	@Produces
	JWTVerifier produceVerifier(){
		return getVerifier();
	}

	@Slf4j
	@Getter
	@RequiredArgsConstructor
	static public class AuthConfig {
		final String clientId;
		final String clientSecret;
		final String clientDomain;
		final String signingAlgorithm;
		final String publicKeyPath;
		final boolean base64EncodedSecret;

		@PostConstruct
		public JWTVerifier loadVerifier(){
			switch ( signingAlgorithm ) {
				case "HS256":case "HS384":case "HS512":
					return loadHSVerifier();
				case "RS256":case "RS384":case "RS512":
					return loadRSVerifier();
				default:
					throw new IllegalStateException( "Invalid algorithm: " + signingAlgorithm );
			}
		}

		JWTVerifier loadHSVerifier(){
			String secret = clientSecret;
			if ( base64EncodedSecret ) {
				final Base64.Decoder decoder = Base64.getDecoder();
				secret = new String( decoder.decode( secret ) );
			}

			return new JWTVerifier( secret, clientId, HTTPS + clientDomain + "/" );
		}

		JWTVerifier loadRSVerifier() {
			try {
				final PublicKey publicKey = readPublicKey(publicKeyPath);
				return new JWTVerifier( publicKey, clientId, HTTPS + clientDomain + "/" );
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

	@RequiredArgsConstructor
	static public class AuthAccount implements Account {

		@Delegate( excludes = NotDelegatedMethods.class )
		final Auth0User user;

		@Override
		public Principal getPrincipal() {
			return user;
		}

		@Override
		public Set<String> getRoles() {
			final Set<String> roles = new HashSet<>();
			roles.addAll( user.getRoles() );
			return roles;
		}

		private interface NotDelegatedMethods {
			List<String> getRoles();
		}
	}
}
