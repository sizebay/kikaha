package io.skullabs.undertow.urouting;

import io.skullabs.undertow.urouting.converter.ConversionException;
import io.skullabs.undertow.urouting.converter.ConverterFactory;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.Headers;

import java.io.Reader;
import java.nio.channels.Channels;
import java.util.Queue;

import trip.spi.Provided;
import trip.spi.Service;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import urouting.api.RoutingException;
import urouting.api.Unserializer;

/**
 * Provides data to a routing method.
 */
@Service
public class RoutingMethodDataProvider {

	@Provided ConverterFactory converterFactory;
	@Provided ServiceProvider provider;

	public <T> T getCookie( final HttpServerExchange exchange, final String cookieParam, final Class<T> clazz ) throws ConversionException, InstantiationException, IllegalAccessException {
		final Cookie cookie = exchange.getRequestCookies().get( cookieParam );
		final String value = cookie.getValue();
		return converterFactory.getConverterFor(clazz).convert(value);
	}

	public <T> T getQueryParam( final HttpServerExchange exchange, final String queryParam, final Class<T> clazz ) throws ConversionException, InstantiationException, IllegalAccessException {
		final Queue<String> queryParams = exchange.getQueryParameters().get( queryParam );
		final String value = queryParams.peek();
		return converterFactory.getConverterFor(clazz).convert(value);
	}
	
	public <T> T getHeaderParam( final HttpServerExchange exchange, final String headerParam, final Class<T> clazz ) throws ConversionException, InstantiationException, IllegalAccessException {
		final Queue<String> headerValues = exchange.getRequestHeaders().get( headerParam );
		final String value = headerValues.peek();
		return converterFactory.getConverterFor(clazz).convert(value);
	}
	
	public <T> T getPathParam( final HttpServerExchange exchange, final String pathParam, final Class<T> clazz ) throws ConversionException, InstantiationException, IllegalAccessException {
		final Queue<String> pathValues = exchange.getPathParameters().get( pathParam );
		final String value = pathValues.peek();
		return converterFactory.getConverterFor(clazz).convert(value);
	}

	public <T> T getBody( final HttpServerExchange exchange, final Class<T> clazz ) throws ServiceProviderException, RoutingException {
		String contentEncoding = exchange.getRequestHeaders().getFirst( Headers.CONTENT_ENCODING_STRING );
		if ( contentEncoding == null )
			contentEncoding = "UTF-8";
		final String contentType = exchange.getRequestHeaders().getFirst( Headers.CONTENT_TYPE_STRING );
		final Unserializer unserializer = provider.load( Unserializer.class, contentType );
		final Reader reader = Channels.newReader( exchange.getRequestChannel(), "UTF-8" );
		return unserializer.unserialize( reader, clazz );
	}
}
