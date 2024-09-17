package kikaha.core.modules.security;

import java.util.List;
import io.undertow.security.api.NotificationReceiver;
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

	String MSG_IMMUTABLE = "You can't change this immutable SecurityContext. See SecurityContextFactory for more details.";
	String MSG_NO_MANUAL_LOGIN = "You can't perform a manual login.";
	String MSG_NOT_SUPPORTED_BY_DEFAULT = "This operation is not supported by default.";

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
	
	// Unused Undertow methods.



	@Override
	default void setAuthenticationRequired() {}

	@Override
	default void authenticationComplete( Account account, String mechanismName, boolean cachingRequired ) {
		throw new UnsupportedOperationException(MSG_NOT_SUPPORTED_BY_DEFAULT);
	}

	@Override
	default void authenticationFailed( String message, String mechanismName ) {
		throw new UnsupportedOperationException(MSG_NOT_SUPPORTED_BY_DEFAULT);
	}

	@Override
	default boolean login(String username, String password) {
		throw new UnsupportedOperationException(MSG_NO_MANUAL_LOGIN);
	}

	@Override
	default void registerNotificationReceiver(NotificationReceiver receiver) {
		throw new UnsupportedOperationException(MSG_IMMUTABLE);
	}

	@Override
	default void removeNotificationReceiver(NotificationReceiver receiver) {
		throw new UnsupportedOperationException(MSG_IMMUTABLE);
	}

	@Override
	default String getMechanismName() {
		throw new UnsupportedOperationException(MSG_NOT_SUPPORTED_BY_DEFAULT);
	}

	@Override
	default io.undertow.security.idm.IdentityManager getIdentityManager() {
		throw new UnsupportedOperationException(MSG_NOT_SUPPORTED_BY_DEFAULT);
	}

	@Override
	default void addAuthenticationMechanism(
			io.undertow.security.api.AuthenticationMechanism mechanism) {
		throw new UnsupportedOperationException(MSG_IMMUTABLE);
	}

	@Override
	default List<io.undertow.security.api.AuthenticationMechanism> getAuthenticationMechanisms() {
		throw new UnsupportedOperationException(MSG_NOT_SUPPORTED_BY_DEFAULT);
	}
}
