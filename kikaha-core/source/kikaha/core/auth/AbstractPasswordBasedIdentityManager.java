package kikaha.core.auth;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import lombok.val;

/**
 * An abstract {@link IdentityManager} implementation designed to handle
 * {@link PasswordCredential} credentials.
 *
 * @author Miere Teixeira
 */
public abstract class AbstractPasswordBasedIdentityManager implements IdentityManager {

	@Override
	public Account verify( String id, Credential credential ) {
		if ( credential instanceof PasswordCredential ) {
			val passwordCredential = (PasswordCredential)credential;
			val password = new String( passwordCredential.getPassword() );
			return retrieveAccountFor( id, password );
		}
		return null;
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

	@Override
	public Account verify( Credential credential ) {
		return null;
	}

	@Override
	public Account verify( Account account ) {
		return account;
	}
}
