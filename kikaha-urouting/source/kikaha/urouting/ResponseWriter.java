package kikaha.urouting;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.IOException;

import kikaha.core.api.conf.Configuration;
import kikaha.urouting.api.Header;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Response;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Serializer;
import trip.spi.Provided;
import trip.spi.Singleton;

/**
 * A helper class to write responses to the HTTP Client.
 */
@Singleton
public class ResponseWriter {

	@Provided
	Configuration kikahaConf;

	@Provided
	SerializerAndUnserializerProvider serializerAndUnserializerProvider;

	/**
	 * Writes a response to HTTP Client informing that no content was available.
	 *
	 * @param exchange
	 */
	public void write( final HttpServerExchange exchange ) {
		sendStatusCode( exchange, 204 );
		exchange.endExchange();
	}

	/**
	 * Serialize and send the {@code response} object to the HTTP Client.
	 *
	 * @param exchange
	 * @param response
	 * @throws RoutingException
	 * @throws IOException
	 * @see Response
	 */
	public void write( final HttpServerExchange exchange, final Response response )
			throws RoutingException, IOException {
		write( exchange, getDefaultContentType(), response );
	}

	/**
	 * Serialize and send the {@code response} object to the HTTP Client.
	 *
	 * @param exchange
	 * @param response
	 * @throws RoutingException
	 * @throws IOException
	 * @see Response
	 */
	public void write( final HttpServerExchange exchange, final Object response )
			throws RoutingException, IOException {
		write( exchange, getDefaultContentType(), response );
	}

	/**
	 * Serialize and send the {@code response} object to the HTTP Client. The
	 * HTTP Status Code will always be 200, in this case. Also, it will send use
	 * <em>UTF-8</em> as default encoding.
	 *
	 * @param exchange
	 * @param contentType
	 * @param response
	 * @throws RoutingException
	 * @throws IOException
	 */
	public void write( final HttpServerExchange exchange, final String contentType, final Object response )
			throws RoutingException, IOException {
		sendStatusCode( exchange, 200 );
		sendContentTypeHeader( exchange, contentType );
		sendBodyResponse( exchange, contentType, getDefaultEncoding(), response );
	}

	/**
	 * Serialize and send the {@code response} object to the HTTP Client.
	 *
	 * @param exchange
	 * @param defaultContentType
	 * @param response
	 * @throws RoutingException
	 * @throws IOException
	 */
	public void write( final HttpServerExchange exchange, final String defaultContentType, final Response response )
			throws RoutingException, IOException {
		final String contentType = response.contentType() != null
			? response.contentType() : defaultContentType;
		sendStatusCode( exchange, response.statusCode() );
		sendHeaders( exchange, response );
		sendContentTypeHeader( exchange, contentType );
		sendBodyResponse( exchange, response );
	}

	void sendBodyResponse( final HttpServerExchange exchange, final Response response )
			throws RoutingException, IOException {
		sendBodyResponse( exchange,
				response.contentType(), response.encoding(), response.entity() );
	}

	void sendBodyResponse(
			final HttpServerExchange exchange, final String contentType,
			final String encoding, final Object serializable )
		throws RoutingException, IOException
	{
		final Serializer serializer = getSerializer( contentType );
		serializer.serialize( serializable, exchange );
		exchange.endExchange();
	}

	Serializer getSerializer( final String contentType ) throws IOException {
		return serializerAndUnserializerProvider.getSerializerFor( contentType, getDefaultContentType() );
	}

	void sendHeaders( final HttpServerExchange exchange, final Response response ) {
		final HeaderMap responseHeaders = exchange.getResponseHeaders();
		for ( final Header header : response.headers() )
			for ( final String value : header.values() )
				sendHeader(responseHeaders, header, value);
	}

	void sendHeader(final HeaderMap responseHeaders, final Header header, final String value) {
		final HttpString headerName = new HttpString( header.name() );
		responseHeaders.add( headerName, value );
	}

	HttpServerExchange sendStatusCode( final HttpServerExchange exchange, final Integer statusCode ) {
		exchange.setResponseCode( statusCode );
		return exchange;
	}

	void sendContentTypeHeader( final HttpServerExchange exchange, final String contentType ) {
		final HeaderMap responseHeaders = exchange.getResponseHeaders();
		if ( !responseHeaders.contains( Headers.CONTENT_TYPE_STRING ) && contentType != null )
			responseHeaders.add( new HttpString( Headers.CONTENT_TYPE_STRING ), contentType );
	}

	String getDefaultEncoding() {
		return kikahaConf.routes().responseEncoding();
	}

	String getDefaultContentType() {
		return Mimes.PLAIN_TEXT;
	}
}
