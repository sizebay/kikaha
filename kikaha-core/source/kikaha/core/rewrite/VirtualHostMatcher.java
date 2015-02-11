package kikaha.core.rewrite;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.Map;

import kikaha.core.url.StringCursor;
import kikaha.core.url.URLMatcher;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.java.Log;

@Log
@RequiredArgsConstructor
public class VirtualHostMatcher implements RequestMatcher {

	final URLMatcher virtualHost;

	@Override
	public Boolean apply( final HttpServerExchange exchange, final Map<String, String> properties )
	{
		val hostHeader = exchange.getRequestHeaders().getFirst( Headers.HOST );
		if ( hostHeader == null ) {
			log.warning( "No HOST header found." );
			return true;
		}
		val host = stripHostFromHeader( hostHeader );
		return virtualHost.matches( host, properties );
	}

	String stripHostFromHeader( final String hostHeader )
	{
		val cursor = new StringCursor( hostHeader );
		if ( !cursor.shiftCursorToNextChar( ':' ) )
			cursor.end();
		else
			cursor.prev();
		val host = cursor.substringUntilCursor();
		return host;
	}

	public static RequestMatcher from( final String path )
	{
		return new VirtualHostMatcher( URLMatcher.compile( path ) );
	}
}
