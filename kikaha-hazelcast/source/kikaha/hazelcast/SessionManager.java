package kikaha.hazelcast;

import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityNotification;
import io.undertow.security.api.SecurityNotification.EventType;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.util.AttachmentKey;
import kikaha.core.auth.AuthenticationRule;
import kikaha.core.auth.DefaultSecurityContextFactory;
import kikaha.core.auth.SecurityContextFactory;
import lombok.val;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.hazelcast.core.IMap;
import com.hazelcast.util.UuidUtil;

@Singleton
public class SessionManager implements SecurityContextFactory {

	public static final String SESSION_ID = "SESSIONID";
	static final AttachmentKey<String> SESSION_KEY = AttachmentKey.create( String.class );

	@Provided
	DefaultSecurityContextFactory defaultContextFactory;

	@Provided( name = SessionCacheProducer.SESSION_CACHE )
	IMap<String, Session> sessionCache;

	@Override
	public SecurityContext createSecurityContextFor(
		final HttpServerExchange exchange, final AuthenticationRule rule )
	{
		final Session session = getSessionFromCacheFor( exchange );
		final SecurityContext context = ( session != null )
			? preAuthenticatedSecurityContext( exchange, session )
			: postAuthenticatedSecurityContext( exchange, rule );
		context.registerNotificationReceiver( new RemoveSessionFromCacheForLoggedOutAccounts() );
		return context;
	}

	Session getSessionFromCacheFor( HttpServerExchange exchange ) {
		final Cookie sessionCookie = getSessionCookie( exchange );
		if ( sessionCookie == null )
			return null;
		final String sessionId = sessionCookie.getValue();
		setSessionAsAttributeToExchange( exchange, sessionId );
		return sessionCache.get( sessionId );
	}

	public void setSessionAsAttributeToExchange( HttpServerExchange exchange, final String sessionId ) {
		exchange.putAttachment( SESSION_KEY, sessionId );
	}

	public Cookie getSessionCookie( HttpServerExchange exchange ) {
		return exchange.getRequestCookies().get( SESSION_ID );
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
		final Session session ) {
		return new PreAuthenticatedFromCacheSecurityContext( session, exchange );
	}

	/**
	 * Generate a valid, pseudo-randomized, session id.<br>
	 * <br>
	 * <b>Note:</b> This code is a copy from {@link com.hazelcast.web.WebFilter}
	 * 
	 * @return
	 */
	public static synchronized String generateSessionId() {
		final String id = UuidUtil.buildRandomUuidString();
		final StringBuilder sb = new StringBuilder( "HZ" );
		final char[] chars = id.toCharArray();
		for ( char c : chars ) {
			if ( c != '-' ) {
				if ( Character.isLetter( c ) )
					sb.append( Character.toUpperCase( c ) );
				else
					sb.append( c );
			}
		}
		return sb.toString();
	}

	/**
	 * Listener class that include new sessions into cache
	 * for any authenticated request.
	 * 
	 * @author Miere Teixeira
	 */
	class IncludeSessionIntoCacheForAuthenticatedAccounts
		implements NotificationReceiver {

		@Override
		public void handleNotification( SecurityNotification notification ) {
			if ( notification.getEventType().equals( EventType.AUTHENTICATED ) ) {
				val account = notification.getAccount();
				val exchange = notification.getExchange();
				val id = generateSessionId();
				sessionCache.put( id, createSessionFrom( account, exchange ) );
				saveSessionCookieFor( exchange, id );
			}
		}

		Session createSessionFrom( final io.undertow.security.idm.Account account, final io.undertow.server.HttpServerExchange exchange ) {
			return Session.from( exchange, account );
		}

		void saveSessionCookieFor( HttpServerExchange exchange, String id ) {
			val cookies = exchange.getResponseCookies();
			val cookie = new CookieImpl( SESSION_ID, id );
			cookie.setPath( "/" );
			cookies.put( SESSION_ID, cookie );
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
		public void handleNotification( SecurityNotification notification ) {
			if ( notification.getEventType().equals( EventType.LOGGED_OUT ) ) {
				String sessionId = notification.getExchange().getAttachment( SESSION_KEY );
				sessionCache.remove( sessionId );
			}
		}
	}
}
