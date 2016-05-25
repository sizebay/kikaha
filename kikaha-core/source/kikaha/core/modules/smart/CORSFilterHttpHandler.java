package kikaha.core.modules.smart;

import java.net.*;
import java.util.*;
import io.undertow.server.*;
import io.undertow.util.*;
import kikaha.core.url.URLMatcher;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@RequiredArgsConstructor
public class CORSFilterHttpHandler implements HttpHandler {

	static final HttpString ACCESS_METHOD = new HttpString( "Access-Control-Request-Method" );
	static final HttpString ACCESS_HEADERS = new HttpString( "Access-Control-Request-Headers" );
	static final HttpString ALLOWED_ORIGIN = new HttpString( "Access-Control-Allow-Origin" );
	static final HttpString ALLOWED_HEADERS = new HttpString( "Access-Control-Allow-Headers" );
	static final Map<String, String> EMPTY_URL_PARAMS = new HashMap<>();

	final CORSConfig config;
	final HttpHandler next;
	final HttpHandler notFound;

	@Override
	public void handleRequest( HttpServerExchange exchange ) throws Exception {
		final boolean isOption = isOption( exchange );
		final String originHost = retrieveOriginHost( exchange );

		if ( !isOption )
			allowOrigin( exchange, originHost );
		else if ( !isAllowedHttpMethod( exchange ) || !isHostAllowed( originHost ) )
			notFound.handleRequest(exchange);
		else
			sendRequiredHeaders( exchange, originHost );
	}

	private void allowOrigin( HttpServerExchange exchange, String originHost ) throws Exception {
		if ( originHost != null ) {
			final HeaderMap responseHeaders = exchange.getResponseHeaders();
			responseHeaders.put(ALLOWED_ORIGIN, originHost);
		}
		next.handleRequest(exchange);
	}

	private boolean isHostAllowed( String originHost ) throws MalformedURLException {
		if ( config.alwaysAllowOrigin) return true;
		else
			for (URLMatcher matcher : config.allowedOrigins )
				if ( matcher.matches( originHost, EMPTY_URL_PARAMS ) )
					return true;
		return false;
	}

	private String retrieveOriginHost( HttpServerExchange exchange ) throws URISyntaxException {
		final HeaderValues refererHeader = exchange.getRequestHeaders().get(Headers.ORIGIN);
		if ( refererHeader == null )
			return null;
		final String referer = refererHeader.getFirst();
		final URI url = new URI(referer);
		return url.getScheme() + "://" + url.getAuthority();
	}

	private static boolean isOption( HttpServerExchange exchange ){
		return Methods.OPTIONS.equals( exchange.getRequestMethod() );
	}

	private boolean isAllowedHttpMethod(HttpServerExchange exchange ){
		final HeaderValues strings = exchange.getRequestHeaders().get(ACCESS_METHOD);
		final String expectedString = strings != null ? strings.getFirst() : "";
		return config.allowedMethods.contains( expectedString );
	}

	private void sendRequiredHeaders( HttpServerExchange exchange, String originHost ) {
		final HeaderMap responseHeaders = exchange.getResponseHeaders();
		responseHeaders.put(ALLOWED_ORIGIN, originHost);
		final HeaderValues allowedHeaders = exchange.getRequestHeaders().get( ACCESS_HEADERS );
		if ( allowedHeaders != null )
			exchange.getResponseHeaders().put( ALLOWED_HEADERS, String.join( ",", (CharSequence[]) allowedHeaders.toArray()) );
	}
}
