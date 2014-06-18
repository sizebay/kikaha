package io.skullabs.undertow.urouting;

import io.skullabs.undertow.urouting.converter.ConversionException;
import io.skullabs.undertow.urouting.converter.ConverterFactory;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.Headers;

import java.io.Reader;
import java.nio.channels.Channels;
import java.util.Queue;

import trip.spi.*;
import urouting.api.RoutingException;
import urouting.api.Unserializer;

/**
 * Provides data to a routing method.
 */
@Service( RoutingMethodDataProvider.class )
public class RoutingMethodDataProvider {

	@Provided
	ConverterFactory converterFactory;
	@Provided
	ServiceProvider provider;

	/**
	 * Get a cookie from request converted to the {@code <T>} type as defined by
	 * {@code clazz} argument.
	 * 
	 * @param exchange
	 * @param cookieParam
	 * @param clazz
	 * @return
	 * @throws ConversionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T getCookieParam( final HttpServerExchange exchange, final String cookieParam, final Class<T> clazz )
			throws ConversionException,
			InstantiationException, IllegalAccessException {
		final Cookie cookie = exchange.getRequestCookies().get( cookieParam );
		final String value = cookie.getValue();
		return converterFactory.getConverterFor( clazz ).convert( value );
	}

	/**
	 * Get a query parameter from request converted to the {@code <T>} type as
	 * defined by {@code clazz} argument.
	 * 
	 * @param exchange
	 * @param queryParam
	 * @param clazz
	 * @return
	 * @throws ConversionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T getQueryParam( final HttpServerExchange exchange, final String queryParam, final Class<T> clazz )
			throws ConversionException, InstantiationException, IllegalAccessException {
		final Queue<String> queryParams = exchange.getQueryParameters().get( queryParam );
		final String value = queryParams.peek();
		return converterFactory.getConverterFor( clazz ).convert( value );
	}

	/**
	 * Get a header parameter from request converted to the {@code <T>} type as
	 * defined by {@code clazz} argument.
	 * 
	 * @param exchange
	 * @param headerParam
	 * @param clazz
	 * @return
	 * @throws ConversionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T getHeaderParam( final HttpServerExchange exchange, final String headerParam, final Class<T> clazz )
			throws ConversionException, InstantiationException, IllegalAccessException {
		final Queue<String> headerValues = exchange.getRequestHeaders().get( headerParam );
		final String value = headerValues.peek();
		return converterFactory.getConverterFor( clazz ).convert( value );
	}

	/**
	 * Get a path parameter from request converted to the {@code <T>} type as
	 * defined by {@code clazz} argument.
	 * 
	 * @param exchange
	 * @param pathParam
	 * @param clazz
	 * @return
	 * @throws ConversionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T getPathParam( final HttpServerExchange exchange, final String pathParam, final Class<T> clazz )
			throws ConversionException, InstantiationException, IllegalAccessException {
		final Queue<String> pathValues = exchange.getPathParameters().get( pathParam );
		final String value = pathValues.peek();
		return converterFactory.getConverterFor( clazz ).convert( value );
	}

	/**
	 * Get the body of current request and convert to {@code <T>} type as
	 * defined by {@code clazz} argument.
	 * 
	 * @param exchange
	 * @param clazz
	 * @return
	 * @throws ServiceProviderException
	 * @throws RoutingException
	 */
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
