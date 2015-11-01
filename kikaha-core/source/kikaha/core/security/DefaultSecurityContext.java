package kikaha.core.security;

import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;

import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DefaultSecurityContext implements SecurityContext {

	private static final String MSG_IMMUTABLE = "You can't change this immuatable SecurityContext. See SecurityContextFactory for more details.";
	private static final String MSG_NO_MANUAL_LOGIN = "You can't perform a manual login.";
	private static final String MSG_NOT_SUPPORTED_BY_DEFAULT = "This operation is not supported by default.";

	private final boolean authenticationRequired = true;

	private Session currentSession = null;
	private boolean authenticated = true;

	@NonNull private final AuthenticationRule rule;
	@NonNull private final HttpServerExchange exchange;
	@NonNull private final SessionStore store;

	@Override
	public boolean authenticate() {
		currentSession = store.createOrRetrieveSession(exchange);
		final Account account = performAuthentication();
		if ( account == null ){
			authenticated = false;
			sendAuthenticationChallenge();
		}
		currentSession.setAuthenticatedAccount( account );
		updateCurrentSession();
		return authenticated;
	}

	private Account performAuthentication() {
		final Iterator<AuthenticationMechanism> iterator = rule.mechanisms().iterator();
		Account account = currentSession.getAuthenticatedAccount();
		while ( account == null && iterator.hasNext() ) {
			final AuthenticationMechanism authMechanism = iterator.next();
			account = authMechanism.authenticate( exchange, rule.identityManagers(), currentSession );
		}
		return account;
	}

	private void sendAuthenticationChallenge() {
		final Iterator<AuthenticationMechanism> iterator = rule.mechanisms().iterator();
		boolean sentChallenge = false;
		while ( !sentChallenge && iterator.hasNext() )
			sentChallenge = iterator.next().sendAuthenticationChallenge( exchange, currentSession );
	}

	@Override
	public void logout() {
		if ( currentSession != null )
			store.invalidateSession(currentSession);
	}

	@Override
	public void updateCurrentSession() {
		if ( currentSession.hasChanged() ) {
			currentSession.flush();
			store.flush( currentSession );
		}
	}

	@Override
	public Account getAuthenticatedAccount() {
		return currentSession != null ? currentSession.getAuthenticatedAccount() : null;
	}

	@Override
	public void setAuthenticationRequired() {}

	@Override
	public void authenticationComplete( Account account, String mechanismName, boolean cachingRequired ) {
		throw new UnsupportedOperationException(MSG_NOT_SUPPORTED_BY_DEFAULT);
	}

	@Override
	public void authenticationFailed( String message, String mechanismName ) {
		throw new UnsupportedOperationException(MSG_NOT_SUPPORTED_BY_DEFAULT);
	}

	@Override
	public boolean login(String username, String password) {
		throw new UnsupportedOperationException(MSG_NO_MANUAL_LOGIN);
	}

	@Override
	public void registerNotificationReceiver(NotificationReceiver receiver) {
		throw new UnsupportedOperationException(MSG_IMMUTABLE);
	}

	@Override
	public void removeNotificationReceiver(NotificationReceiver receiver) {
		throw new UnsupportedOperationException(MSG_IMMUTABLE);
	}

	@Override
	public String getMechanismName() {
		throw new UnsupportedOperationException(MSG_NOT_SUPPORTED_BY_DEFAULT);
	}

	@Override
	public IdentityManager getIdentityManager() {
		throw new UnsupportedOperationException(MSG_NOT_SUPPORTED_BY_DEFAULT);
	}

	@Override
	public void addAuthenticationMechanism(
			io.undertow.security.api.AuthenticationMechanism mechanism) {
		throw new UnsupportedOperationException(MSG_IMMUTABLE);
	}

	@Override
	public List<io.undertow.security.api.AuthenticationMechanism> getAuthenticationMechanisms() {
		throw new UnsupportedOperationException(MSG_NOT_SUPPORTED_BY_DEFAULT);
	}
}
