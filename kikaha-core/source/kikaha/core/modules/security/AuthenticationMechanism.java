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

	Account authenticate(
			final HttpServerExchange exchange,
			final Iterable<IdentityManager> identityManagers, Session session );

	boolean sendAuthenticationChallenge( final HttpServerExchange exchange, Session session );

	default Account verify( Iterable<IdentityManager> identityManagers, Credential credential ) {
		Account account = null;
		final Iterator<IdentityManager> iterator = identityManagers.iterator();
		while ( account == null && iterator.hasNext() )
			account = iterator.next().verify( credential );
		return account;
	}
}
