package kikaha.hazelcast;

import io.undertow.server.HttpServerExchange;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;

import kikaha.core.security.AbstractCookieSessionStore;
import kikaha.core.security.DefaultSession;
import kikaha.core.security.Session;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;

@Singleton
public class HazelcastSessionStore extends AbstractCookieSessionStore {

	private static String MAC_ADDRESS = retrieveCurrentMacAddress();

	@Provided
	@Source( "session-cache" )
	IMap<String, Session> sessionCache;

	@Provided
	@Source( "session-cache" )
	IdGenerator idGenerator;

	private static String retrieveCurrentMacAddress(){
		try {
			final InetAddress localHost = InetAddress.getLocalHost();
			final NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
			return new String( convertMACBytesToString( networkInterface.getHardwareAddress() ) );
		} catch (SocketException | UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	private static String convertMACBytesToString( byte[] mac ){
		final StringBuilder buffer = new StringBuilder();
		for (final byte element : mac)
			buffer.append(String.format("%02X", element));
		return buffer.toString();
	}

	@Override
	public Session createOrRetrieveSession( HttpServerExchange exchange ) {
		final String sessionId = retrieveSessionIdFrom( exchange );
		Session session = getSessionFromCache( sessionId );
		if ( session == null )
			synchronized ( sessionCache ) {
				if ( ( session = getSessionFromCache( sessionId ) ) == null ) {
					session = createNewSession();
					storeSession( session.getId(), session );
					attachSessionCookie( exchange, session.getId() );
				}
			}
		return session;
	}

	protected void storeSession(String sessionId, Session session) {
		sessionCache.put(sessionId, session);
	}

	protected Session getSessionFromCache( String sessionId ) {
		if ( sessionId == null )
			return null;
		return sessionCache.get( sessionId );
	}

	protected Session createNewSession() {
		final String id = MAC_ADDRESS + idGenerator.newId() + new Date().getTime();
		return new DefaultSession( id );
	}

	@Override
	public void invalidateSession(Session session) {
		sessionCache.remove(session.getId());
	}

	@Override
	public void flush(Session session) {
		sessionCache.put(session.getId(), session);
	}

	public Collection<Session> retrieveAllSessions(){
		return sessionCache.values();
	}
}
