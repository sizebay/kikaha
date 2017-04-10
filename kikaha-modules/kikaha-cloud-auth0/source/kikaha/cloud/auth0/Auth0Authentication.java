package kikaha.cloud.auth0;

import javax.annotation.PostConstruct;
import javax.inject.*;
import java.util.Deque;
import com.auth0.*;
import com.auth0.jwt.JWTVerifier;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import kikaha.cloud.auth0.Auth0.AuthAccount;
import kikaha.core.modules.security.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Extract the credentials from the current request and ask to Auth0 for authorization and authentication.
 */
@Slf4j
@Singleton
public class Auth0Authentication implements AuthenticationMechanism {

	public static final String STATE = "state", NONCE = "nonce";

	@Inject FormAuthenticationConfiguration formAuthenticationConfiguration;
	@Inject Auth0.AuthConfig auth0Config;
	@Inject Auth0Client auth0Client;
	JWTVerifier verifier;

	@PostConstruct
	public void loadVerifier(){
		verifier = auth0Config.loadVerifier();
	}

	@Override
	public Account authenticate( HttpServerExchange exchange, Iterable<IdentityManager> identityManagers, Session session ) {
		try {
			final String stateFromStorage = (String) session.getAttribute( STATE );
			final Deque<String> stateFromRequests = exchange.getQueryParameters().get( STATE );
			if ( isAuthenticationCallbackPage( exchange ) && isTokenPresent( stateFromStorage, stateFromRequests ) )
				return retrieveAccount( session );
			if ( stateFromRequests != null && !stateFromRequests.isEmpty() )
				session.setAttribute( STATE, stateFromRequests.getFirst() );
		} catch ( RuntimeException unexpectedException ) {
			handleFailure( exchange, unexpectedException );
		}
		return null;
	}

	private boolean isTokenPresent( String stateFromStorage, Deque<String> statesFromRequest ) {
		boolean valid = false;
		if ( statesFromRequest != null && !statesFromRequest.isEmpty() && stateFromStorage != null ) {
			final String
				stateFromRequest = statesFromRequest.getFirst(),
				nonceFromRequest = QueryParamUtils.parseFromQueryParams(stateFromRequest, "nonce"),
				nonceFromStorage = QueryParamUtils.parseFromQueryParams(stateFromStorage, "nonce");
			valid = nonceFromRequest != null && !nonceFromRequest.isEmpty() && nonceFromRequest.equals(nonceFromStorage);
		}
		return valid;
	}

	private boolean isAuthenticationCallbackPage( HttpServerExchange serverExchange ) {
		return formAuthenticationConfiguration.getCallbackUrl().equals( serverExchange.getRelativePath() );
	}

	private AuthAccount retrieveAccount( Session session ){
		final Tokens tokens = auth0Client.getTokens( session.getId(), formAuthenticationConfiguration.getSuccessPage() );
		final Auth0User userProfile = auth0Client.getUserProfile( tokens );
		final AuthAccount authAccount = new AuthAccount( userProfile );
		session.setAuthenticatedAccount( authAccount );
		return authAccount;
	}

	void handleFailure( HttpServerExchange exchange, RuntimeException unexpectedException ) {
		log.error( "Could not execute the Auth0 login: " + unexpectedException.getMessage(), unexpectedException );
		redirectTo( exchange, formAuthenticationConfiguration.getErrorPage() );
	}

	@Override
	public boolean sendAuthenticationChallenge( HttpServerExchange exchange, Session session ) {
		log.warn( "Not logged in. Redirecting to " + formAuthenticationConfiguration.getLoginPage() );
		redirectTo( exchange, formAuthenticationConfiguration.getLoginPage() );
		return true;
	}

	void redirectTo( HttpServerExchange exchange, String location ){
		if ( !exchange.isResponseStarted() ) {
			exchange.setStatusCode( StatusCodes.TEMPORARY_REDIRECT );
			exchange.getResponseHeaders().add( Headers.LOCATION, location );
		}
	}
}
