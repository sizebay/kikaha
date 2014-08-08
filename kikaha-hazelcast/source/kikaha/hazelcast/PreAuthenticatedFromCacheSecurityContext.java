package kikaha.hazelcast;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityNotification;
import io.undertow.security.api.SecurityNotification.EventType;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class PreAuthenticatedFromCacheSecurityContext
	implements SecurityContext {

	final List<NotificationReceiver> notificationReceivers = new ArrayList<>();
	final Session session;
	final HttpServerExchange exchange;

	@Override
	public boolean authenticate() {
		notify( EventType.AUTHENTICATED );
		return true;
	}

	@Override
	public boolean login( String username, String password ) {
		return true;
	}

	@Override
	public boolean isAuthenticationRequired() {
		return true;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public Account getAuthenticatedAccount() {
		return session.account();
	}

	@Override
	public void registerNotificationReceiver( NotificationReceiver receiver ) {
		notificationReceivers.add( receiver );
	}

	@Override
	public void removeNotificationReceiver( NotificationReceiver receiver ) {
		notificationReceivers.remove( receiver );
	}

	@Override
	public void logout() {
		notify( EventType.LOGGED_OUT );
	}

	@Override
	public void setAuthenticationRequired() {
	}

	@Override
	public void addAuthenticationMechanism( AuthenticationMechanism mechanism ) {
	}

	@Override
	public List<AuthenticationMechanism> getAuthenticationMechanisms() {
		return null;
	}

	@Override
	public String getMechanismName() {
		return null;
	}

	@Override
	public IdentityManager getIdentityManager() {
		return null;
	}

	@Override
	public void authenticationComplete( Account account, String mechanismName, boolean cachingRequired ) {
	}

	@Override
	public void authenticationFailed( String message, String mechanismName ) {
	}

	void notify( EventType eventType ) {
		for ( val receiver : notificationReceivers )
			receiver.handleNotification( createNotification( eventType ) );
	}

	SecurityNotification createNotification( EventType eventType ) {
		return new SecurityNotification(
			exchange, eventType, session.account(),
			null, false, null, false );
	}
}
