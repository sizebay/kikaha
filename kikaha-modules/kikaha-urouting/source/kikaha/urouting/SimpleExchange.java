package kikaha.urouting;

import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

/**
 *
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
}
