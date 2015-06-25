package kikaha.core.rewrite;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutoHTTPSRedirectHandler implements HttpHandler {

	private static final int MOVED_PERMANENTLY = 301;

	final HttpHandler next;

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		final String scheme = exchange.getRequestScheme();
		if ( scheme.equals( "http") ){
			exchange.setResponseCode(MOVED_PERMANENTLY);
			exchange.getResponseHeaders().put(Headers.LOCATION, createNewLocation(exchange));
		} else
			next.handleRequest(exchange);
	}

	private String createNewLocation(final HttpServerExchange exchange) {
		return new StringBuilder()
			.append("https://")
			.append(exchange.getHostAndPort())
			.append(exchange.getRelativePath())
			.toString();
	}

}
