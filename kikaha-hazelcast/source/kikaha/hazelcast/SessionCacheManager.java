package kikaha.hazelcast;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.util.AttachmentKey;
import lombok.Getter;
import lombok.val;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * A service responsible to handle sessions persisted in Hazelcast data-grid.
 *
 * @author Miere Teixeira
 */
@Singleton
public class SessionCacheManager {

	public static final AttachmentKey<String> SESSION_KEY = AttachmentKey.create( String.class );
	public static final String SESSION_ID = "SESSIONID";
	public static final String SESSION_CACHE = "session-cache";

	@Provided
	HazelcastInstance hazelcast;

	@Provided
	HazelcastConfiguration configuration;

	@Getter( lazy = true )
	private final IMap<String, AuthenticatedSession> sessionCache = produceSessionCache();

	IMap<String, AuthenticatedSession> produceSessionCache() {
		val hazelcastConfig = hazelcast.getConfig();
		val expirableConfig = hazelcastConfig.getMapConfig( SESSION_CACHE );
		expirableConfig.setTimeToLiveSeconds( configuration.sessionTimeToLive() );
		return hazelcast.getMap( SESSION_CACHE );
	}

	public AuthenticatedSession getOrCreateSessionFor( final HttpServerExchange exchange ) {
		AuthenticatedSession session = getSession( exchange );
		if ( session == null )
			session = createValidationSessionForExchange( exchange );
		return session;
	}

	/**
	 * Use data persisted in current {@link HttpServerExchange} to retrieve a
	 * valid {@link AuthenticatedSession} from it.
	 *
	 * @param exchange
	 * @return
	 */
	public AuthenticatedSession getSession( final HttpServerExchange exchange ) {
		val sessionCookie = getSessionCookie( exchange );
		if ( sessionCookie == null )
			return null;
		val sessionId = sessionCookie.getValue();
		setSessionAsAttributeToExchange( exchange, sessionId );
		val session = getSession( sessionId );
		if ( !isValidSessionForExchange( session, exchange ) )
			return null;
		return session;
	}

	/**
	 * Retrieve an {@link AuthenticatedSession} that matches the argument
	 * {@code sessionId}.
	 *
	 * @param sessionId
	 * @return
	 */
	public AuthenticatedSession getSession( final String sessionId ) {
		return getSessionCache().get( sessionId );
	}

	Cookie getSessionCookie( final HttpServerExchange exchange ) {
		return exchange.getRequestCookies().get( SESSION_ID );
	}

	void setSessionAsAttributeToExchange( final HttpServerExchange exchange, final String sessionId ) {
		exchange.putAttachment( SESSION_KEY, sessionId );
	}

	boolean isValidSessionForExchange( final AuthenticatedSession session, final HttpServerExchange exchange ) {
		if ( session == null )
			return false;
		val newSession = createValidationSessionForExchange( exchange );
		return newSession.userAgent().equals( session.userAgent() );
	}

	AuthenticatedSession createValidationSessionForExchange( final HttpServerExchange exchange ) {
		final AuthenticatedSession session = AuthenticatedSession.from( exchange );
		saveSessionCookieFor( exchange, session.getId() );
		return session;
	}

	/**
	 * Create and memorize an {@link AuthenticatedSession} at the internal
	 * cache. Also, set the session at the sent {@link HttpServerExchange}
	 * argument for further usage.
	 *
	 * @param account
	 * @param exchange
	 * @return
	 */
	public AuthenticatedSession memorize( final Account account, final HttpServerExchange exchange ) {
		val id = generateANewId();
		val session = AuthenticatedSession.from( id, exchange, account );
		saveSessionCookieFor( exchange, id );
		return memorizeOrUpdate( session );
	}

	String generateANewId() {
		return SessionID.generateSessionId();
	}

	void saveSessionCookieFor( final HttpServerExchange exchange, final String id ) {
		val cookies = exchange.getResponseCookies();
		val cookie = new CookieImpl( SESSION_ID, id );
		cookie.setPath( "/" );
		cookies.put( SESSION_ID, cookie );
	}

	/**
	 * Memorize an {@link AuthenticatedSession} at the internal cache.
	 *
	 * @param session
	 * @return
	 */
	public AuthenticatedSession memorizeOrUpdate( final AuthenticatedSession session ) {
		return getSessionCache().put( session.getId(), session );
	}

	/**
	 * Remove the {@link AuthenticatedSession} persisted at
	 * {@link HttpServerExchange} from internal cache.
	 *
	 * @param exchange
	 */
	public void removeSessionFrom( final HttpServerExchange exchange ) {
		val sessionId = exchange.getAttachment( SESSION_KEY );
		if ( sessionId != null ) {
			remove( sessionId );
			exchange.removeAttachment( SESSION_KEY );
		}
	}

	/**
	 * Remove the {@link AuthenticatedSession} from internal cache.
	 *
	 * @param sessionId
	 */
	public void remove( final String sessionId ) {
		getSessionCache().remove( sessionId );
	}

}