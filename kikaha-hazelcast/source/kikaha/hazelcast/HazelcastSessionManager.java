package kikaha.hazelcast;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionManager;

import java.util.Set;

import lombok.Getter;
import lombok.val;
import trip.spi.Provided;
import trip.spi.Singleton;

@Getter
@Singleton
public class HazelcastSessionManager implements SessionManager {

	private final String deploymentName = getClass().getSimpleName();

	@Provided
	SessionCacheManager cacheManager;
	int defaultSessionTimeout;

	@Override
	public Session createSession( final HttpServerExchange serverExchange, final SessionConfig sessionCookieConfig ) {
		final Account account = getAccountFrom( serverExchange );
		return cacheManager.memorize( account, serverExchange );
	}

	Account getAccountFrom( final HttpServerExchange serverExchange ) {
		val context = serverExchange.getSecurityContext();
		if ( context == null )
			return SessionAccount.empty();
		return context.getAuthenticatedAccount();
	}

	@Override
	public Session getSession( final HttpServerExchange serverExchange, final SessionConfig sessionCookieConfig ) {
		return cacheManager.getSession( serverExchange );
	}

	@Override
	public Session getSession( final String sessionId ) {
		return cacheManager.getSession( sessionId );
	}

	@Override
	public void registerSessionListener( final SessionListener listener ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeSessionListener( final SessionListener listener ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDefaultSessionTimeout( final int timeout ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getTransientSessions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getActiveSessions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getAllSessions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}
}
