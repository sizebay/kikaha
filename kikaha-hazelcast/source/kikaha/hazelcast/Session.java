package kikaha.hazelcast;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.Serializable;
import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class Session implements Serializable {

	private static final long serialVersionUID = -1702911555724253987L;

	final String userAgent;
	final String host;
	final SessionAccount account;

	public static Session from( HttpServerExchange exchange, Account account ) {
		val headers = exchange.getRequestHeaders();
		val hostWhenProxied = headers.getFirst( Headers.X_FORWARDED_FOR );
		val host = hostWhenProxied != null
			? hostWhenProxied : extractHostAddressFrom( exchange );
		return new Session(
			headers.getFirst( Headers.USER_AGENT ),
			host, SessionAccount.from( account ) );
	}

	public static Session from( HttpServerExchange exchange ) {
		val headers = exchange.getRequestHeaders();
		return new Session(
			headers.getFirst( Headers.USER_AGENT ), null, null );
	}

	static String extractHostAddressFrom( HttpServerExchange exchange ) {
		final InetSocketAddress peerAddress = (InetSocketAddress)exchange.getConnection().getPeerAddress();
		return peerAddress.getAddress().getHostAddress();
	}
}
