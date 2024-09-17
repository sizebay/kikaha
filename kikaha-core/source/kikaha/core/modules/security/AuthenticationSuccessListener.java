package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;

/**
 * Listen for authentication-success events.
 */
public interface AuthenticationSuccessListener {

	/**
	 * Called when the authentication mechanism was able to authenticate the user.
	 *
	 * @param exchange the current request
	 * @param session the current logged in session
	 * @param currentAuthMechanism the authentication mechanism that was responsible for the authentication process
	 */
	void onAuthenticationSuccess( HttpServerExchange exchange, Session session, AuthenticationMechanism currentAuthMechanism );
}
