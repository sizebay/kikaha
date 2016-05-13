package kikaha.core.modules.security;

import java.net.*;
import java.util.*;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;

public abstract class AbstractCookieSessionStore implements SessionStore {

	protected static String MAC_ADDRESS = retrieveCurrentMacAddress();

	final String cookieName;

	public AbstractCookieSessionStore() {
		cookieName = "JSESSIONID";
	}

	protected abstract Session getSessionFromCache(String sessionId);

	protected Session createAndStoreNewSession(String sessionId, HttpServerExchange exchange ){
		Session session = getSessionFromCache(sessionId);
		if (session == null) {
			session = new DefaultSession( sessionId );
			storeSession(session.getId(), session);
			attachSessionCookie(exchange, session.getId());
		}
		return session;
	}

	protected abstract void storeSession(String id, Session session);

	protected void attachSessionCookie( HttpServerExchange exchange, String sessionId ) {
		final Cookie cookie = new CookieImpl( this.cookieName, sessionId ).setPath( "/" );
		exchange.setResponseCookie( cookie );
	}

	protected String retrieveSessionIdFrom( HttpServerExchange exchange ) {
		final Cookie cookie = exchange.getRequestCookies().get( cookieName );
		return cookie != null ? cookie.getValue() : createNewSessionId();
	}

	protected abstract String createNewSessionId();

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
}
