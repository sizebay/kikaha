package kikaha.hazelcast;

import com.hazelcast.core.IMap;
import io.undertow.server.HttpServerExchange;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;

import com.hazelcast.core.IdGenerator;
import kikaha.core.modules.security.AbstractCookieSessionStore;
import kikaha.core.modules.security.DefaultSession;
import kikaha.core.modules.security.Session;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class HazelcastSessionStore extends AbstractCookieSessionStore {

	private static String MAC_ADDRESS = retrieveCurrentMacAddress();

	@Inject
	@Named( "session-cache" )
	IMap<String, Session> sessionCache;

	@Inject
	@Named( "session-cache" )
	IdGenerator idGenerator;

	private static String retrieveCurrentMacAddress(){
		try {
			final NetworkInterface networkInterface = getNetworkInterface();
			return new String( convertMACBytesToString( networkInterface.getHardwareAddress() ) );
		} catch ( final SocketException e ) {
			throw new RuntimeException(e);
		}
	}

	private static NetworkInterface getNetworkInterface() throws SocketException {
		final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while ( networkInterfaces.hasMoreElements() ) {
			final NetworkInterface networkInterface = networkInterfaces.nextElement();
			final byte[] hardwareAddress = networkInterface.getHardwareAddress();
			if ( hardwareAddress != null && hardwareAddress.length > 4 )
				return networkInterface;
		}
		return null;
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
		final String id = createNewSessionId();
		return new DefaultSession( id );
	}

	public String createNewSessionId() {
		return MAC_ADDRESS + idGenerator.newId() + new Date().getTime();
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
