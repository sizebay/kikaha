package kikaha.hazelcast;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class WrappedSecurityContext implements SecurityContext {

	final List<AuthenticationEventInterceptor> interceptorsBeforeAuthenticate = new ArrayList<>();
	final SecurityContext target;

	@Override
	public boolean authenticate() {
		notifyBeforeAuthenticate();
		return target.authenticate();
	}

	public void notifyBeforeAuthenticate( final AuthenticationEventInterceptor interceptor ) {
		interceptorsBeforeAuthenticate.add( interceptor );
	}

	void notifyBeforeAuthenticate() {
		for ( val interceptor : interceptorsBeforeAuthenticate )
			interceptor.intercep();
	}

	@Override
	public boolean login( final String username, final String password ) {
		return target.login( username, password );
	}

	@Override
	public void logout() {
		target.logout();
	}

	@Override
	public void setAuthenticationRequired() {
		target.setAuthenticationRequired();
	}

	@Override
	public boolean isAuthenticationRequired() {
		return target.isAuthenticationRequired();
	}

	@Override
	public void addAuthenticationMechanism( final AuthenticationMechanism mechanism ) {
		target.addAuthenticationMechanism( mechanism );
	}

	@Override
	public List<AuthenticationMechanism> getAuthenticationMechanisms() {
		return target.getAuthenticationMechanisms();
	}

	@Override
	public boolean isAuthenticated() {
		return target.isAuthenticated();
	}

	@Override
	public Account getAuthenticatedAccount() {
		return target.getAuthenticatedAccount();
	}

	@Override
	public String getMechanismName() {
		return target.getMechanismName();
	}

	@Override
	public IdentityManager getIdentityManager() {
		return target.getIdentityManager();
	}

	@Override
	public void authenticationComplete( final Account account, final String mechanismName, final boolean cachingRequired ) {
		target.authenticationComplete( account, mechanismName, cachingRequired );
	}

	@Override
	public void authenticationFailed( final String message, final String mechanismName ) {
		target.authenticationFailed( message, mechanismName );
	}

	@Override
	public void registerNotificationReceiver( final NotificationReceiver receiver ) {
		target.registerNotificationReceiver( receiver );
	}

	@Override
	public void removeNotificationReceiver( final NotificationReceiver receiver ) {
		target.removeNotificationReceiver( receiver );
	}
}

interface AuthenticationEventInterceptor {
	void intercep();
}