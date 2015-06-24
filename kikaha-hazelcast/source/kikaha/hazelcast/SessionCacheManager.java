package kikaha.hazelcast;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.util.AttachmentKey;
import kikaha.hazelcast.config.HazelcastConfiguration;
import lombok.val;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
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
	public static final AttachmentKey<AuthenticatedSession> CURRENT_SESSION = AttachmentKey.create( AuthenticatedSession.class );
	public static final String SESSION_ID = "SESSIONID";
	public static final String SESSION_CACHE = "session-cache";

	@Provided
	HazelcastInstance hazelcast;

	@Provided
	HazelcastConfiguration configuration;

	IMap<String, AuthenticatedSession> sessionCache;

	public AuthenticatedSession getOrCreateSessionFor( final HttpServerExchange exchange ) {
		AuthenticatedSession session = getSession( exchange );
		if ( session == null )
			session = createValidationSessionForExchange( exchange );
		return session;
	}

	private IMap<String, AuthenticatedSession> getSessionCache(){
		if ( sessionCache == null )
			synchronized( this ){
				if ( sessionCache == null )
					sessionCache = produceSessionCache();
			}
		return sessionCache;
	}

	IMap<String, AuthenticatedSession> produceSessionCache() {
		final Config hazelcastConfig = hazelcast.getConfig();
		final MapConfig expirableConfig = hazelcastConfig.getMapConfig( SESSION_CACHE );
		expirableConfig.setTimeToLiveSeconds( configuration.sessionTimeToLive() );
		return hazelcast.getMap( SESSION_CACHE );
	}

	/**
	 * Use data persisted in current {@link HttpServerExchange} to retrieve a
	 * valid {@link AuthenticatedSession} from it.
	 *
	 * @param exchange
	 * @return
	 */
	public AuthenticatedSession getSession( final HttpServerExchange exchange ) {
		AuthenticatedSession session = exchange.getAttachment( CURRENT_SESSION );
		if ( session == null )
			session = produceASessionFromCookieInExchange( exchange );
		return session;
	}

	AuthenticatedSession produceASessionFromCookieInExchange(
		final HttpServerExchange exchange ) {
		val sessionCookie = getSessionCookie( exchange );
		if ( sessionCookie == null )
			return null;
		val sessionId = sessionCookie.getValue();
		val session = getSession( sessionId );
		if ( !isValidSessionForExchange( session, exchange ) )
			return null;
		setSessionAsAttributeToExchange( exchange, session );
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

	void setSessionAsAttributeToExchange( final HttpServerExchange exchange, final AuthenticatedSession session ) {
		exchange.putAttachment( CURRENT_SESSION, session );
		exchange.putAttachment( SESSION_KEY, session.getId() );
	}

	boolean isValidSessionForExchange( final AuthenticatedSession session, final HttpServerExchange exchange ) {
		if ( session == null )
			return false;
		val newSession = createValidationSessionForExchange( exchange );
		return newSession.userAgent().equals( session.userAgent() );
	}

	AuthenticatedSession createValidationSessionForExchange( final HttpServerExchange exchange ) {
		final AuthenticatedSession session = AuthenticatedSession.from( exchange );
		setSessionAsAttributeToExchange( exchange, session );
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
		return memorizeOrUpdate( exchange, session );
	}

	public AuthenticatedSession memorizeOrUpdate(
			final HttpServerExchange exchange,
			final AuthenticatedSession session ) {
		saveSessionCookieFor( exchange, session );
		return memorizeOrUpdate( session );
	}

	String generateANewId() {
		return SessionID.generateSessionId();
	}

	void saveSessionCookieFor( final HttpServerExchange exchange, final AuthenticatedSession session ) {
		val cookies = exchange.getResponseCookies();
		val cookie = new CookieImpl( SESSION_ID, session.getId() );
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