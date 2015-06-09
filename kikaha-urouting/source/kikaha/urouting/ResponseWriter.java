package kikaha.urouting;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.IOException;

import kikaha.urouting.api.Header;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Response;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Serializer;
import lombok.extern.java.Log;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

/**
 * A helper class to write responses to the HTTP Client.
 */
@Log
@Singleton
public class ResponseWriter {

	@Provided
	ServiceProvider provider;

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
	 * @throws ServiceProviderException
	 * @throws RoutingException
	 * @throws IOException
	 * @see Response
	 */
	public void write( final HttpServerExchange exchange, final Response response )
			throws ServiceProviderException, RoutingException, IOException {
		write( exchange, getDefaultContentType(), response );
	}

	/**
	 * Serialize and send the {@code response} object to the HTTP Client.
	 *
	 * @param exchange
	 * @param response
	 * @throws ServiceProviderException
	 * @throws RoutingException
	 * @throws IOException
	 * @see Response
	 */
	public void write( final HttpServerExchange exchange, final Object response )
			throws ServiceProviderException, RoutingException, IOException {
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
	 * @throws ServiceProviderException
	 * @throws RoutingException
	 * @throws IOException
	 */
	public void write( final HttpServerExchange exchange, final String contentType, final Object response )
			throws ServiceProviderException, RoutingException, IOException {
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
	 * @throws ServiceProviderException
	 * @throws RoutingException
	 * @throws IOException
	 */
	public void write( final HttpServerExchange exchange, final String defaultContentType, final Response response )
			throws ServiceProviderException, RoutingException, IOException {
		final String contentType = response.contentType() != null
			? response.contentType() : defaultContentType;
		sendStatusCode( exchange, response.statusCode() );
		sendHeaders( exchange, response );
		sendContentTypeHeader( exchange, contentType );
		sendBodyResponse( exchange, response );
	}

	void sendBodyResponse( final HttpServerExchange exchange, final Response response )
			throws ServiceProviderException, RoutingException, IOException {
		sendBodyResponse( exchange,
				response.contentType(), response.encoding(), response.entity() );
	}

	void sendBodyResponse(
			final HttpServerExchange exchange, final String contentType,
			final String encoding, final Object serializable )
		throws ServiceProviderException, RoutingException, IOException
	{
		final Serializer serializer = getSerializer( contentType );
		serializer.serialize( serializable, exchange );
		exchange.endExchange();
	}

	Serializer getSerializer( final String contentType )
			throws ServiceProviderException {
		return getSerializer( contentType, Mimes.PLAIN_TEXT );
	}

	Serializer getSerializer( final String contentType, final String defaultContentType )
			throws ServiceProviderException {
		Serializer serializer = provider.load( Serializer.class, contentType );
		if ( serializer == null ) {
			log.warning( "No serializer found for " + contentType + ". Falling back to " + defaultContentType );
			serializer = provider.load( Serializer.class, defaultContentType );
		}
		return serializer;
	}

	void sendHeaders( final HttpServerExchange exchange, final Response response ) {
		final HeaderMap responseHeaders = sendContentTypeHeader( exchange, response.contentType() );
		for ( final Header header : response.headers() )
			for ( final String value : header.values() )
				sendHeader(responseHeaders, header, value);
	}

	void sendHeader(final HeaderMap responseHeaders,
			final Header header, final String value) {
		responseHeaders.add( new HttpString( header.name() ), value );
	}

	HttpServerExchange sendStatusCode( final HttpServerExchange exchange, final Integer statusCode ) {
		exchange.setResponseCode( statusCode );
		return exchange;
	}

	HeaderMap sendContentTypeHeader( final HttpServerExchange exchange, final String contentType ) {
		final HeaderMap responseHeaders = exchange.getResponseHeaders();
		if ( responseHeaders.contains( Headers.CONTENT_TYPE_STRING ) )
			responseHeaders.add( new HttpString( Headers.CONTENT_TYPE_STRING ), contentType );
		return responseHeaders;
	}

	String getDefaultEncoding() {
		return "UTF-8";
	}

	String getDefaultContentType() {
		return Mimes.PLAIN_TEXT;
	}
}
