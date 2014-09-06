package kikaha.hazelcast;

import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityNotification;
import io.undertow.security.api.SecurityNotification.EventType;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SessionManager;
import kikaha.core.auth.AuthenticationRule;
import kikaha.core.auth.DefaultSecurityContextFactory;
import kikaha.core.auth.SecurityContextFactory;
import lombok.val;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton
public class HazelcastSecurityContextFactory implements SecurityContextFactory {

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
		final AuthenticatedSession session = sessionCache.getSession( exchange );
		final SecurityContext context = ( session != null )
			? preAuthenticatedSecurityContext( exchange, session )
			: postAuthenticatedSecurityContext( exchange, rule );
		context.registerNotificationReceiver( new RemoveSessionFromCacheForLoggedOutAccounts() );
		exchange.putAttachment( SessionManager.ATTACHMENT_KEY, sessionManager );
		return context;
	}

	public SecurityContext postAuthenticatedSecurityContext( final HttpServerExchange exchange, final AuthenticationRule rule ) {
		final SecurityContext securityContext = createSecurityContextWithDefaultFactory( exchange, rule );
		securityContext.registerNotificationReceiver( createReceiverToIncludeSessionIntoCache() );
		return securityContext;
	}

	IncludeSessionIntoCacheForAuthenticatedAccounts createReceiverToIncludeSessionIntoCache() {
		return new IncludeSessionIntoCacheForAuthenticatedAccounts();
	}

	SecurityContext createSecurityContextWithDefaultFactory( final HttpServerExchange exchange, final AuthenticationRule rule ) {
		return defaultContextFactory.createSecurityContextFor( exchange, rule );
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
	class IncludeSessionIntoCacheForAuthenticatedAccounts
		implements NotificationReceiver {

		@Override
		public void handleNotification( final SecurityNotification notification ) {
			if ( notification.getEventType().equals( EventType.AUTHENTICATED ) ) {
				val account = notification.getAccount();
				val exchange = notification.getExchange();
				sessionCache.memorize( account, exchange );
			}
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
}
