package io.skullabs.undertow.urouting;

import io.skullabs.undertow.urouting.api.Header;
import io.skullabs.undertow.urouting.api.Mimes;
import io.skullabs.undertow.urouting.api.Response;
import io.skullabs.undertow.urouting.api.RoutingException;
import io.skullabs.undertow.urouting.api.Serializer;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;

import lombok.extern.java.Log;

import org.xnio.channels.StreamSinkChannel;

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
	 * @param contentType
	 * @param response
	 * @throws ServiceProviderException
	 * @throws RoutingException
	 * @throws IOException
	 */
	public void write( final HttpServerExchange exchange, final String contentType, final Response response )
			throws ServiceProviderException, RoutingException, IOException {
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
			throws ServiceProviderException, RoutingException, IOException {
		if ( serializable == null )
			return;
		
		final StreamSinkChannel channel = exchange.getResponseChannel();
		final Writer writer = Channels.newWriter( channel, encoding );
		final Serializer serializer = getSerializer( contentType );
		serializer.serialize( serializable, UncloseableWriterWrapper.wrap( writer ) );
		writer.flush();
	}

	Serializer getSerializer( String contentType )
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

	HttpServerExchange sendStatusCode( final HttpServerExchange exchange, Integer statusCode ) {
		exchange.setResponseCode( statusCode );
		return exchange;
	}

	HeaderMap sendContentTypeHeader( final HttpServerExchange exchange, String contentType ) {
		final HeaderMap responseHeaders = exchange.getResponseHeaders();
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
