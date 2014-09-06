package kikaha.hazelcast;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.Headers;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class AuthenticatedSession implements Serializable, Session {

	private static final long serialVersionUID = -1702911555724253987L;

	final Date creationTime = new Date();
	final Map<String, Object> attributes = new ConcurrentHashMap<>();

	final String id;
	final String userAgent;
	final String host;
	final SessionAccount account;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void requestDone( HttpServerExchange serverExchange ) {
	}

	@Override
	public long getCreationTime() {
		return creationTime.getTime();
	}

	@Override
	public long getLastAccessedTime() {
		return creationTime.getTime();
	}

	@Override
	public void setMaxInactiveInterval( int interval ) {
	}

	@Override
	public int getMaxInactiveInterval() {
		return 0;
	}

	@Override
	public Object getAttribute( String name ) {
		return attributes.get( name );
	}

	@Override
	public Set<String> getAttributeNames() {
		return attributes.keySet();
	}

	@Override
	public Object setAttribute( String name, Object value ) {
		return attributes.put( name, value );
	}

	@Override
	public Object removeAttribute( String name ) {
		return attributes.remove( name );
	}

	@Override
	public void invalidate( HttpServerExchange exchange ) {
	}

	@Override
	public SessionManager getSessionManager() {
		return null;
	}

	@Override
	public String changeSessionId( HttpServerExchange exchange, SessionConfig config ) {
		return id;
	}

	public static AuthenticatedSession from(
		final String id, final HttpServerExchange exchange, final Account account ) {
		val headers = exchange.getRequestHeaders();
		val hostWhenProxied = headers.getFirst( Headers.X_FORWARDED_FOR );
		val host = hostWhenProxied != null
			? hostWhenProxied : extractHostAddressFrom( exchange );
		return new AuthenticatedSession(
			id, headers.getFirst( Headers.USER_AGENT ),
			host, SessionAccount.from( account ) );
	}

	public static AuthenticatedSession from( HttpServerExchange exchange ) {
		val headers = exchange.getRequestHeaders();
		return new AuthenticatedSession(
			null, headers.getFirst( Headers.USER_AGENT ), null, null );
	}

	static String extractHostAddressFrom( HttpServerExchange exchange ) {
		final InetSocketAddress peerAddress = (InetSocketAddress)exchange.getConnection().getPeerAddress();
		return peerAddress.getAddress().getHostAddress();
	}
}
