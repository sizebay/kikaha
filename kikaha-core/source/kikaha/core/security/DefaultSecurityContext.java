package kikaha.core.security;

import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;

import java.util.Iterator;
import java.util.List;

import kikaha.core.auth.AuthenticationRule;
import kikaha.core.security.OutcomeResponse.Outcome;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DefaultSecurityContext implements SecurityContext {

	private static final String MSG_IMMUTABLE = "You can't change this immuatable SecurityContext. See SecurityContextFactory for more details.";
	private static final String MSG_NO_MANUAL_LOGIN = "You can't perform a manual login.";
	private static final String MSG_NOT_SUPPORTED_BY_DEFAULT = "This operation is not supported by default.";

	final boolean authenticationRequired = true;

	Session currentSession = null;

	@NonNull final AuthenticationRule rule;
	@NonNull final HttpServerExchange exchange;
	@NonNull final SessionStore store;

	@Override
	public boolean authenticate() {
		currentSession = store.createOrRetrieveSession(exchange);
		final OutcomeResponse response = performAuthentication();
		currentSession.setAuthenticatedAccount( response.account );
		return isAuthenticated();
	}

	private OutcomeResponse performAuthentication() {
		final Iterator<AuthenticationMechanism> iterator = rule.mechanisms().iterator();
		OutcomeResponse response = null;
		while ( isNotAuthenticated( response ) && iterator.hasNext() ) {
			final AuthenticationMechanism authMechanism = iterator.next();
			response = authMechanism.authenticate( exchange, rule.identityManager() );
		}
		return response;
	}

	static private boolean isNotAuthenticated( OutcomeResponse response ) {
		return response == null || !Outcome.AUTHENTICATED.equals( response.outcome );
	}

	@Override
	public void logout() {
		if ( currentSession != null )
			store.invalidateSession(currentSession);
	}

	@Override
	public Account getAuthenticatedAccount() {
		return currentSession != null ? currentSession.getAuthenticatedAccount() : null;
	}

	@Override
	public boolean isAuthenticated() {
		return currentSession != null && currentSession.getAuthenticatedAccount() != null;
	}

	@Override
	public void authenticationComplete( Account account, String mechanismName, boolean cachingRequired ) {}

	@Override
	public void authenticationFailed( String message, String mechanismName ) {}

	@Override
	public void setAuthenticationRequired() {
		throw new UnsupportedOperationException( MSG_IMMUTABLE );
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
