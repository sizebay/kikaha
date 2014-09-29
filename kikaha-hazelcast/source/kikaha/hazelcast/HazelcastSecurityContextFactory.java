package kikaha.hazelcast;

import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityNotification;
import io.undertow.security.api.SecurityNotification.EventType;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SessionManager;
import io.undertow.util.Headers;
import kikaha.core.auth.AuthenticationRule;
import kikaha.core.auth.DefaultSecurityContextFactory;
import kikaha.core.auth.SecurityContextFactory;
import lombok.RequiredArgsConstructor;
import lombok.val;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton
public class HazelcastSecurityContextFactory implements SecurityContextFactory {

	static final String LAST_LOCATION = "LAST_LOCATION";

	@Provided
	DefaultSecurityContextFactory defaultContextFactory;

	@Provided
	SessionCacheManager sessionCache;

	@Provided
	HazelcastSessionManager sessionManager;

	@Override
	public SecurityContext createSecurityContextFor(
		final HttpServerExchange exchange, final AuthenticationRule rule )
	{
		final AuthenticatedSession session = sessionCache.getOrCreateSessionFor( exchange );
		final SecurityContext context = isAuthenticatedSession( session )
			? preAuthenticatedSecurityContext( exchange, session )
			: postAuthenticatedSecurityContext( exchange, session, rule );
		context.registerNotificationReceiver( new RemoveSessionFromCacheForLoggedOutAccounts() );
		exchange.putAttachment( SessionManager.ATTACHMENT_KEY, sessionManager );
		return context;
	}

	boolean isAuthenticatedSession( final AuthenticatedSession session ) {
		return session != null
			&& session.account() != null
			&& session.account().getPrincipal() != null;
	}

	public SecurityContext postAuthenticatedSecurityContext(
			final HttpServerExchange exchange,
			final AuthenticatedSession session,
			final AuthenticationRule rule ) {
		final WrappedSecurityContext securityContext = createSecurityContextWithDefaultFactory( exchange, rule );
		securityContext.registerNotificationReceiver( createReceiverToIncludeSessionIntoCache() );
		securityContext.notifyBeforeAuthenticate( new MemorizerOfCurrentLocation( exchange, session ) );
		return securityContext;
	}

	IncluderOfSessionIntoCacheForAuthenticatedAccounts createReceiverToIncludeSessionIntoCache() {
		return new IncluderOfSessionIntoCacheForAuthenticatedAccounts();
	}

	WrappedSecurityContext createSecurityContextWithDefaultFactory( final HttpServerExchange exchange, final AuthenticationRule rule ) {
		return new WrappedSecurityContext(
			defaultContextFactory.createSecurityContextFor( exchange, rule ) );
	}

	PreAuthenticatedFromCacheSecurityContext preAuthenticatedSecurityContext( final HttpServerExchange exchange,
		final AuthenticatedSession session ) {
		return new PreAuthenticatedFromCacheSecurityContext( session, exchange );
	}

	/**
	 * Listener class that include new sessions into cache for any authenticated
	 * request. It will always create a new session when user are authenticated.
	 *
	 * @author Miere Teixeira
	 */
	class IncluderOfSessionIntoCacheForAuthenticatedAccounts
		implements NotificationReceiver {

		@Override
		public void handleNotification( final SecurityNotification notification ) {
			if ( notification.getEventType().equals( EventType.AUTHENTICATED ) ) {
				val account = notification.getAccount();
				val exchange = notification.getExchange();
				handleDefaultResponse( exchange, account );
			}
		}

		public boolean handleDefaultResponse( final HttpServerExchange exchange, final Account account ) {
			tryToSendRedirect( exchange );
			sessionCache.memorize( account, exchange );
			exchange.endExchange();
			return true;
		}

		void tryToSendRedirect( final HttpServerExchange exchange ) {
			try {
				final AuthenticatedSession session = sessionCache.getSession( exchange );
				final String location = (String)session.getAttribute( LAST_LOCATION );
				sendRedirect( exchange, location );
				session.removeAttribute( LAST_LOCATION );
				sessionCache.memorizeOrUpdate( session );
			} catch ( final Throwable e ) {
				e.printStackTrace();
			}
		}

		void sendRedirect( final HttpServerExchange exchange, final String location ) {
			exchange.setResponseCode( 303 );
			final String loc = exchange.getRequestScheme() + "://" + exchange.getHostAndPort() + location;
			exchange.getResponseHeaders().put( Headers.LOCATION, loc );
		}
	}

	/**
	 * Listener that removes session from cache whenever a users have
	 * logged out from the system.
	 *
	 * @author Miere Teixeira
	 */
	class RemoveSessionFromCacheForLoggedOutAccounts
		implements NotificationReceiver {

		@Override
		public void handleNotification( final SecurityNotification notification ) {
			if ( notification.getEventType().equals( EventType.LOGGED_OUT ) )
				sessionCache.removeSessionFrom( notification.getExchange() );
		}
	}

	/**
	 * Interceptor for {@link SecurityContext#authenticate()} ensuring that will
	 * memorize the current location for further redirection usage.
	 *
	 * @author Miere Teixeira
	 */
	@RequiredArgsConstructor
	class MemorizerOfCurrentLocation implements AuthenticationEventInterceptor {

		final HttpServerExchange exchange;
		final AuthenticatedSession session;

		@Override
		public void intercep() {
			if ( session.getAttribute( LAST_LOCATION ) == null ) {
				final String location = exchange.getRelativePath();
				session.setAttribute( LAST_LOCATION, location );
				sessionCache.memorizeOrUpdate( session );
			}
		}
	}
}
