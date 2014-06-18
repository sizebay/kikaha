package io.skullabs.undertow.urouting;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.Writer;
import java.nio.channels.Channels;

import org.xnio.channels.StreamSinkChannel;

import trip.spi.Provided;
import trip.spi.Service;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import urouting.api.Header;
import urouting.api.Response;
import urouting.api.RoutingException;
import urouting.api.Serializer;

@Service
public class ResponseWriter {

	@Provided ServiceProvider provider;

	public void write( final HttpServerExchange exchange, final Response response )
			throws ServiceProviderException, RoutingException {
		exchange.setResponseCode( response.statusCode() );
		sendHeaders(exchange, response);
		serializeReponseEntity(exchange, response);
	}

	void serializeReponseEntity( final HttpServerExchange exchange, final Response response)
			throws ServiceProviderException, RoutingException {
		final Object serializable = response.entity();
		final StreamSinkChannel channel = exchange.getResponseChannel();
		final Writer writer = Channels.newWriter(channel, response.encoding());
		final Serializer serializer  = provider.load( Serializer.class , response.contentType() );
		serializer.serialize( serializable, writer);
	}

	void sendHeaders( final HttpServerExchange exchange, final Response response ) {
		final HeaderMap responseHeaders = exchange.getResponseHeaders();
		responseHeaders.add( new HttpString( Headers.CONTENT_TYPE_STRING ), response.contentType() );
		for ( final Header header : response.headers() )
			for ( final String value : header.values() )
				responseHeaders.add( new HttpString( header.name() ), value);
	}
}
