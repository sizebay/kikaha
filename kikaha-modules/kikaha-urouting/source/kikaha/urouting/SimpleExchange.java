package kikaha.urouting;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import lombok.RequiredArgsConstructor;

/**
 * Represents an incoming request. Most of time, is just a very tiny layer above
 * Undertow's API providing to developers an easy to use API for their daily routines.
 */
@RequiredArgsConstructor(staticName = "wrap")
public class SimpleExchange {

	final HttpServerExchange exchange;
	final RoutingMethodParameterReader parameterReader;
	final RoutingMethodResponseWriter responseWriter;

	/**
	 * Return the host, and also the port if this request was sent to a non-standard port. In general
	 * this will just be the value of the Host header.
	 * <p>
	 * If this resolves to an IPv6 address it *will*  be enclosed by square brackets. The return
	 * value of this method is suitable for inclusion in a URL.
	 *
	 * @return The host and port part of the destination address
	 */
	public String getHostAndPort(){
		return exchange.getHostAndPort();
	}

	/**
	 * Get the request URI scheme.  Normally this is one of {@code http} or {@code https}.
	 *
	 * @return the request URI scheme
	 */
	public String getRequestScheme() {
		return exchange.getRequestScheme();
	}

	/**
	 * Get the HTTP request method.  Normally this is one of the strings listed in {@link io.undertow.util.Methods}.
	 *
	 * @return the HTTP request method
	 */
	public HttpString getHttpMethod() {
		return exchange.getRequestMethod();
	}

	/**
	 * Get the request relative path.  This is the path which should be evaluated by the current handler.
	 *
	 * @return the request relative path
	 */
	public String getRelativePath() {
		return exchange.getRelativePath();
	}
}