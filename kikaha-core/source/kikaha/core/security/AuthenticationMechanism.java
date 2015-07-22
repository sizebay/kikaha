package kikaha.core.security;

import java.util.Iterator;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.server.HttpServerExchange;

/**
 * Authentication mechanism interface based on {@code io.undertow.security.api.AuthenticationMechanism}
 * original implementation.
 */
public interface AuthenticationMechanism {

	OutcomeResponse authenticate(
			final HttpServerExchange exchange, final Iterable<IdentityManager> identityManagers );

	default Account verify( Iterable<IdentityManager> identityManagers, Credential credential ) {
		Account account = null;
		Iterator<IdentityManager> iterator = identityManagers.iterator();
		while ( account == null && iterator.hasNext() )
			account = iterator.next().verify( credential );
		return account;
	}
}
