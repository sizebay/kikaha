package kikaha.core.rewrite;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import kikaha.core.url.StringCursor;
import kikaha.core.url.URLMatcher;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
class VirtualHostPredicate implements Function<HttpServerExchange,Map<String,String>> {

	final URLMatcher expectedVirtualHost;

	public VirtualHostPredicate( final String virtualHost ) {
		expectedVirtualHost = URLMatcher.compile( virtualHost );
	}

	@Override
	public Map<String,String> apply( final HttpServerExchange exchange ) {
		val hostHeader = exchange.getRequestHeaders().getFirst( Headers.HOST );
		val host = stripHostFromHeader( hostHeader );
		val properties = new HashMap<String, String>();
		if ( expectedVirtualHost.matches( host, properties ) )
			return properties;
		return null;
	}

	String stripHostFromHeader( final String hostHeader ) {
		val cursor = new StringCursor( hostHeader );
		if ( !cursor.shiftCursorToNextChar( ':' ) )
			cursor.end();
		val host = cursor.substringUntilCursor();
		return host;
	}
}