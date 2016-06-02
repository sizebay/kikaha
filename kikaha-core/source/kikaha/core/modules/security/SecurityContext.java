package kikaha.core.modules.security;

import io.undertow.security.idm.Account;
/**
 * The security context. This context is attached to the exchange and holds
 * all security related information.
 * <br>
 * It basically extends the functionality of Undertow's {@link io.undertow.security.api.SecurityContext}.
 *
 * @see DefaultSecurityContext
 */
public interface SecurityContext extends io.undertow.security.api.SecurityContext {

	/**
	 * Updates the {@link Account} that represents the current logged in user.
	 * This method use to be the same as {@link Session#setAuthenticatedAccount(Account)}.
	 *
	 * @param account
	 */
	void setAuthenticatedAccount(Account account);

	/**
	 * @return the {@link Session} object that holds information about the current user
	 */
	Session getCurrentSession();

	/**
	 * Defines another {@link Session} object to hold information about the current user.
	 * @param session
	 */
	void setCurrentSession( Session session );

	/**
	 * Flush the current session. Under-the-hood, it send changed data on the {@link Session}
	 * object to the configured {@link SessionStore}.
	 */
	void updateCurrentSession();
}
