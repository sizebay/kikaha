package kikaha.core.modules.security;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;

/**
 * Authentication mechanism interface based on {@code io.undertow.security.api.AuthenticationMechanism}
 * original implementation.
 */
public interface AuthenticationMechanism {

	/**
	 * Execute the actual authentication process. This method are in charge to ask {@link IdentityManager}s
	 * for a verified {@link Account}.
	 *
	 * @param exchange
	 * @param identityManagers
	 * @param session
	 * @return
	 */
	Account authenticate(
			final HttpServerExchange exchange,
			final Iterable<IdentityManager> identityManagers, Session session );

	boolean sendAuthenticationChallenge( final HttpServerExchange exchange, Session session );

	default void configure(
		SecurityConfiguration securityConfiguration,
		DefaultAuthenticationConfiguration authenticationConfiguration ) {}

	/**
	 * Defines the priority for this {@link AuthenticationMechanism}. This value is used to define
	 * the order {@link AuthenticationMechanism}s will call {@link #configure} method. The mechanism
	 * with higher priority will be the first one, second higher will be second one, etc...
	 *
	 * By default, mechanisms have {@code Integer.MIN_VALUE} as default priority.
	 *
	 * @return
	 */
	default int priority(){
		return Integer.MIN_VALUE;
	}
}
