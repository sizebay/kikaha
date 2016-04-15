package kikaha.core.modules.security;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.PasswordCredential;

/**
 * An abstract {@link IdentityManager} implementation designed to handle
 * {@link PasswordCredential} credentials.
 *
 * @author Miere Teixeira
 */
public abstract class AbstractPasswordBasedIdentityManager implements IdentityManager {

	@Override
	public Account verify( final Credential credential ) {
		Account account = null;
		if ( credential instanceof UsernameAndPasswordCredential ) {
			final UsernameAndPasswordCredential passwordCredential = (UsernameAndPasswordCredential)credential;
			account = retrieveAccountFor( passwordCredential.getUsername(), passwordCredential.getPassword() );
		}
		return account;
	}

	/**
	 * Retrieves a valid {@link Account} for a given {@code id} and
	 * {@code passoword} credentials. Developers are encouraged to implement
	 * this method in order to avoid implements unneeded methods inherited from
	 * {@link IdentityManager}.
	 *
	 * @param id
	 * @param password
	 * @return
	 */
	public abstract Account retrieveAccountFor( String id, final String password );
}
