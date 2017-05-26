package kikaha.core.modules.security;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.server.HttpServerExchange;

import java.util.Iterator;

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
}
