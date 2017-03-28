package kikaha.cloud.auth0;

import static com.auth0.jwt.pem.PemReader.readPublicKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.auth0.*;
import com.auth0.jwt.JWTVerifier;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.security.*;

/**
 *
 */
public class JWTAuthenticationMechanism implements AuthenticationMechanism {

	@Inject Auth0.AuthConfig authConfig;
	@Inject Auth0Client auth0Client;
	JWTVerifier verifier;

	@PostConstruct
	public void loadVerifier(){
		switch ( authConfig.signingAlgorithm ) {
			case "HS256":case "HS384":case "HS512":
				verifier = loadHSVerifier();
				break;
			case "RS256":case "RS384":case "RS512":
				verifier = loadRSVerifier();
				break;
			default:
				throw new IllegalStateException( "Invalid algorithm: " + authConfig.signingAlgorithm );
		}
	}

	JWTVerifier loadHSVerifier(){
		String secret = authConfig.clientSecret;
		if ( authConfig.base64EncodedSecret ) {
			final Base64.Decoder decoder = Base64.getDecoder();
			secret = new String( decoder.decode( secret ) );
		}

		return new JWTVerifier( secret, authConfig.clientId, authConfig.issuer );
	}

	JWTVerifier loadRSVerifier() {
		try {
			final PublicKey publicKey = readPublicKey(authConfig.publicKeyPath);
			return new JWTVerifier( publicKey, authConfig.clientId, authConfig.issuer );
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Account authenticate(HttpServerExchange exchange, Iterable<IdentityManager> identityManagers, Session session) {
		final Tokens tokens = fetchTokens(session);
		final Auth0User userProfile = auth0Client.getUserProfile( tokens );


		final JWTCredential credential = new JWTCredential();
		return verify( identityManagers, credential );
	}

	Tokens fetchTokens( Session session ){
		return auth0Client.getTokens( session.getId(), authConfig.redirectOnSuccess );
	}

	@Override
	public boolean sendAuthenticationChallenge(HttpServerExchange exchange, Session session) {
		throw new UnsupportedOperationException("sendAuthenticationChallenge not implemented yet!");
	}
}
