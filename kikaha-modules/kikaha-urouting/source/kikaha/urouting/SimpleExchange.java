package kikaha.urouting;

import java.io.IOException;
import java.util.*;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.*;
import kikaha.urouting.api.*;
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

	/**
	 * Returns a mutable map of query parameters.
	 *
	 * @return The query parameters
	 */
	public Map<String, Deque<String>> getQueryParameters() {
		return exchange.getQueryParameters();
	}

	/**
	 * Get a simple query parameter, named {@code name}, converted to the type {@code type}.
	 *
	 * @param name
	 * @param type
	 * @param <T>
	 * @return
	 * @throws IOException if anything goes wrong during the conversion process
	 */
	public <T> T getQueryParameter(String name, Class<T> type) throws IOException  {
		try {
			return parameterReader.getQueryParam(exchange, name, type);
		} catch ( IllegalAccessException | InstantiationException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Retrieve all path parameters related to this request.
	 *
	 * @return all path parameters.
	 */
	public Map<String, String> getPathParameters() {
		return parameterReader.getPathParams( exchange );
	}

	/**
	 * Get a path parameter from request converted to the {@code <T>} type as
	 * defined by {@code clazz} argument.
	 *
	 * @param name
	 * @param type
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	public <T> T getPathParameter(String name, Class<T> type) throws IOException {
		try {
			return parameterReader.getPathParam( exchange, name, type );
		} catch (ConversionException | InstantiationException | IllegalAccessException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Get the response headers.
	 *
	 * @return the response headers
	 */
	public HeaderMap getHeaderParameters() {
		return exchange.getRequestHeaders();
	}

	/**
	 * Get a header parameter from request converted to the {@code <T>} type as
	 * defined by {@code clazz} argument.
	 *
	 * @param name
	 * @param type
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	public <T> T getHeaderParameter(String name, Class<T> type) throws IOException {
		try {
			return parameterReader.getHeaderParam( exchange, name, type );
		} catch (ConversionException | InstantiationException | IllegalAccessException e) {
			throw new IOException(e);
		}
	}

	/**
	 * @return A mutable map of request cookies
	 */
	public Map<String, Cookie> getCookieParameters() {
		 return exchange.getRequestCookies();
	}

	/**
	 * Get a cookie from request converted to the {@code <T>} type as defined by
	 * {@code clazz} argument.
	 *
	 * @param name
	 * @param type
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	public <T> T getCookieParameter(String name, Class<T> type) throws IOException {
		try {
			return parameterReader.getCookieParam( exchange, name, type );
		} catch (ConversionException | InstantiationException | IllegalAccessException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Get the body of current request and convert to {@code <T>} type as
	 * defined by {@code clazz} argument.<br>
	 * <br>
	 * <p>
	 * It searches for {@link Unserializer} implementations to convert sent data
	 * from client into the desired object. The "Content-Type" header is the
	 * information needed to define which {@link Unserializer} should be used to
	 * decode the sent data into an object. When no {@link Unserializer} is
	 * found it uses the {@code defaultConsumingContentType} argument to seek
	 * another one.
	 *
	 * @param type
	 * @param contentType
	 * @param <T>
	 * @return
	 * @throws IOException if no decoder/unserializer was found.
	 */
	public <T> T getRequestBody(Class<T> type, String contentType) throws IOException {
		return parameterReader.getBody(exchange, type, contentType);
	}

	/**
	 * Serialize and send the {@code response} object to the HTTP Client.
	 *
	 * @param response
	 * @throws IOException
	 */
	public void sendResponse(Response response) throws IOException {
		responseWriter.write( exchange, response );
	}

	/**
	 * Serialize and send the {@code response} object to the HTTP Client.
	 *
	 * @param response
	 * @param contentType
	 * @throws IOException
	 */
	public void sendResponse(Object response, String contentType) throws IOException {
		responseWriter.write( exchange, contentType, response );
	}
}
