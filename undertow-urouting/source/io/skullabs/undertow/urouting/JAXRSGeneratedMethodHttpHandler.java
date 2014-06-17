package io.skullabs.undertow.urouting;

import io.skullabs.undertow.urouting.converter.ConversionException;
import io.skullabs.undertow.urouting.converter.ConverterFactory;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;

import java.io.Reader;
import java.nio.channels.Channels;
import java.util.Queue;

import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class JAXRSGeneratedMethodHttpHandler implements HttpHandler {
	
	@Provided ConverterFactory converterFactory;
	@Provided ServiceProvider provider;

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		
	}
	
	public <T> T getValue( HttpServerExchange exchange, CookieParam param, Class<T> clazz ) throws ConversionException, InstantiationException, IllegalAccessException {
		Cookie cookie = exchange.getRequestCookies().get(param.value());
		String value = cookie.getValue();
		return converterFactory.getConverterFor(clazz).convert(value);
	}
	
	public <T> T getValue( HttpServerExchange exchange, QueryParam param, Class<T> clazz ) throws ConversionException, InstantiationException, IllegalAccessException {
		Queue<String> queryParams = exchange.getQueryParameters().get(param.value());
		String value = queryParams.peek();
		return converterFactory.getConverterFor(clazz).convert(value);
	}
	
	public <T> T getValue( HttpServerExchange exchange, HeaderParam param, Class<T> clazz ) throws ConversionException, InstantiationException, IllegalAccessException {
		Queue<String> headerValues = exchange.getRequestHeaders().get(param.value());
		String value = headerValues.peek();
		return converterFactory.getConverterFor(clazz).convert(value);
	}
	
	public <T> T getValue( HttpServerExchange exchange, PathParam param, Class<T> clazz ) throws ConversionException, InstantiationException, IllegalAccessException {
		Queue<String> pathValues = exchange.getPathParameters().get(param.value());
		String value = pathValues.peek();
		return converterFactory.getConverterFor(clazz).convert(value);
	}
	
	public <T> T getValue( HttpServerExchange exchange, Class<T> clazz ) throws ServiceProviderException, RoutingException {
		HeaderValues headerValues = exchange.getRequestHeaders().get( Headers.CONTENT_TYPE_STRING );
		String contentType = headerValues.getFirst();
		Unserializer unserializer = provider.load( Unserializer.class, contentType );
		Reader reader = Channels.newReader( exchange.getRequestChannel(), "UTF-8" );
		return unserializer.unserialize( reader, clazz );
	}
}
