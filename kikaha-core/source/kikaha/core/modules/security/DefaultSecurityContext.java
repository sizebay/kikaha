package kikaha.core.modules.security;

import static kikaha.core.modules.security.SecurityEventListener.SecurityEventType.*;
import java.util.Iterator;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import lombok.*;

/**
 * A per-request object that groups security-related information.
 */
@Getter
@RequiredArgsConstructor
public class DefaultSecurityContext implements SecurityContext {

	private AuthenticationMechanism currentAuthMechanism = null;
	private Session currentSession = null;
	private boolean authenticated = false;

	@NonNull private final AuthenticationRule rule;
	@NonNull private final HttpServerExchange exchange;
	@NonNull private final SecurityConfiguration configuration;
	@NonNull private final boolean authenticationRequired;

	@Override
	public boolean authenticate() {
		authenticated = true;

		final Account account = performAuthentication();
		if ( account == null ){
			authenticated = false;
			getCurrentSession().setAuthenticatedAccount( account );
			configuration.getAuthenticationFailureListener().onAuthenticationFailure( exchange, getCurrentSession(), currentAuthMechanism );
		} else {
			getCurrentSession().setAuthenticatedAccount( account );
			configuration.getAuthenticationSuccessListener().onAuthenticationSuccess(exchange, getCurrentSession(), currentAuthMechanism);
			notifySecurityEvent( LOGIN );
		}

		updateCurrentSession();
		return authenticated;
	}

	private Account performAuthentication() {
		final Iterator<AuthenticationMechanism> iterator = rule.mechanisms().iterator();
		Account account = getCurrentSession().getAuthenticatedAccount();
		while ( account == null && iterator.hasNext() ) {
			currentAuthMechanism = iterator.next();
			account = currentAuthMechanism.authenticate( exchange, rule.identityManagers(), getCurrentSession() );
		}
		return account;
	}

	@Override
	public void logout() {
		final Session currentSession = getCurrentSession();
		if ( currentSession != null ) {
			configuration.getSessionStore().invalidateSession( currentSession );
			configuration.getSessionIdManager().expiresSessionId( exchange );
			notifySecurityEvent( LOGOUT );
		}
	}

	@Override
	public void updateCurrentSession() {
		if ( currentSession != null && currentSession.hasChanged() ) {
			try { configuration.getSessionStore().flush( currentSession ); }
			finally { currentSession.flush(); }
		}
	}

	public Session getCurrentSession(){
		if ( currentSession == null )
			currentSession = configuration.getSessionStore().createOrRetrieveSession(exchange, configuration.getSessionIdManager());
		return currentSession;
	}

	public void setCurrentSession( Session session ){
		this.currentSession = session;
	}

	@Override
	public Account getAuthenticatedAccount() {
		return currentSession != null ? currentSession.getAuthenticatedAccount() : null;
	}

	@Override
	public void setAuthenticatedAccount(Account account){
		if ( getCurrentSession() != null ) {
			getCurrentSession().setAuthenticatedAccount(account);
			notifySecurityEvent( PROFILE_UPDATED );
		}
	}

	void notifySecurityEvent(SecurityEventListener.SecurityEventType eventType) {
		for ( SecurityEventListener eventListener : configuration.getEventListeners() ) {
			eventListener.onEvent( eventType, exchange, getCurrentSession() );
		}
	}
}
