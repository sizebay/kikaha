package kikaha.cloud.auth0;

import static kikaha.cloud.auth0.Auth0.*;

import javax.inject.*;
import java.util.Deque;
import com.auth0.*;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import kikaha.cloud.auth0.Auth0.AuthAccount;
import kikaha.core.modules.security.*;
import kikaha.core.util.Redirect;
import lombok.extern.slf4j.Slf4j;

/**
 * Extract the credentials from the current request and ask to Auth0 for authorization and authentication.
 */
@Slf4j
@Singleton
public class Auth0Authentication implements AuthenticationMechanism {

	@Inject FormAuthenticationConfiguration formAuthConfig;
	@Inject Auth0Client auth0Client;

	@Override
	public Account authenticate( HttpServerExchange exchange, Iterable<IdentityManager> identityManagers, Session session ) {
		try {
			final String stateFromStorage = (String) session.getAttribute( STATE );
			final String stateFromRequests = getQueryParam( exchange, STATE );
			if ( isAuthenticationCallbackPage( exchange ) && stateMatches( stateFromStorage, stateFromRequests ) )
				return retrieveAccount( exchange, session );
		} catch ( RuntimeException unexpectedException ) {
			handleFailure( exchange, unexpectedException );
		}
		return null;
	}

	private boolean stateMatches( String stateFromStorage, String stateFromRequest ) {
		boolean valid = false;
		if ( stateFromRequest != null && stateFromStorage != null ) {
			final String
				nonceFromRequest = QueryParamUtils.parseFromQueryParams(stateFromRequest, "nonce"),
				nonceFromStorage = QueryParamUtils.parseFromQueryParams(stateFromStorage, "nonce");
			valid = nonceFromRequest != null && !nonceFromRequest.isEmpty() && nonceFromRequest.equals(nonceFromStorage);
		}
		return valid;
	}

	private boolean isAuthenticationCallbackPage( HttpServerExchange serverExchange ) {
		return formAuthConfig.getCallbackUrl().equals( serverExchange.getRelativePath() );
	}

	private AuthAccount retrieveAccount( HttpServerExchange exchange, Session session ){
		try {
			final String code = getQueryParam( exchange, CODE );
			final String redirectUrl = exchange.getRequestScheme() + "://" + exchange.getHostAndPort() + formAuthConfig.getCallbackUrl();
			final Tokens tokens = auth0Client.getTokens( code, redirectUrl );
			final Auth0User userProfile = auth0Client.getUserProfile( tokens );
			final AuthAccount authAccount = new AuthAccount( userProfile );
			session.setAuthenticatedAccount( authAccount );
			session.setAttribute( TOKEN, tokens );
			return authAccount;
		} finally {
			redirectTo( exchange, formAuthConfig.getSuccessPage() );
		}
	}

	void handleFailure( HttpServerExchange exchange, RuntimeException unexpectedException ) {
		log.error( "Could not execute the Auth0 login: " + unexpectedException.getMessage(), unexpectedException );
		redirectTo( exchange, formAuthConfig.getErrorPage() );
	}

	@Override
	public boolean sendAuthenticationChallenge( HttpServerExchange exchange, Session session ) {
		log.warn( "Not logged in. Redirecting to " + formAuthConfig.getLoginPage() );
		redirectTo( exchange, formAuthConfig.getLoginPage() );
		return true;
	}

	void redirectTo( HttpServerExchange exchange, String location ){
		if ( !exchange.isResponseStarted() ) {
			Redirect.to(exchange, location);
		}
	}

	String getQueryParam( HttpServerExchange exchange, String name ) {
		final Deque<String> values = exchange.getQueryParameters().get( name );
		if ( values != null )
			return values.getFirst();
		return null;
	}
}
