package io.skullabs.undertow.urouting;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;

import java.io.Writer;
import java.nio.channels.Channels;

import org.xnio.channels.StreamSinkChannel;
import trip.spi.*;
import urouting.api.*;
import urouting.api.RoutingException;
import urouting.api.Serializer;

/**
 * A helper class to write responses to the HTTP Client.
 */
@Service
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
	 * Serialize and send the {@code response} object to the HTTP Client. The
	 * HTTP Status Code will always be 200, in this case. Also, it will send use
	 * <em>UTF-8</em> as default encoding.
	 * 
	 * @param exchange
	 * @param contentType
	 * @param response
	 * @throws ServiceProviderException
	 * @throws RoutingException
	 */
	public void write( final HttpServerExchange exchange, final String contentType, final Object response )
			throws ServiceProviderException, RoutingException {
		sendStatusCode( exchange, 200 );
		sendContentTypeHeader( exchange, contentType );
		sendBodyResponse( exchange, contentType, "UTF-8", response );
	}

	/**
	 * Serialize and send the {@code response} object to the HTTP Client.
	 * 
	 * @param exchange
	 * @param response
	 * @throws ServiceProviderException
	 * @throws RoutingException
	 * @see Response
	 */
	public void write( final HttpServerExchange exchange, final Response response )
			throws ServiceProviderException, RoutingException {
		sendStatusCode( exchange, response.statusCode() );
		sendHeaders( exchange, response );
		sendBodyResponse( exchange, response );
	}

	void sendBodyResponse( final HttpServerExchange exchange, final Response response )
			throws ServiceProviderException, RoutingException {
		sendBodyResponse( exchange,
				response.contentType(), response.encoding(), response.entity() );
	}

	void sendBodyResponse(
			final HttpServerExchange exchange, final String contentType,
			final String encoding, final Object serializable )
			throws ServiceProviderException, RoutingException {
		final StreamSinkChannel channel = exchange.getResponseChannel();
		final Writer writer = Channels.newWriter( channel, encoding );
		final Serializer serializer = provider.load( Serializer.class, contentType );
		serializer.serialize( serializable, writer );
	}

	void sendHeaders( final HttpServerExchange exchange, final Response response ) {
		final HeaderMap responseHeaders = sendContentTypeHeader( exchange, response.contentType() );
		for ( final Header header : response.headers() )
			for ( final String value : header.values() )
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
}
