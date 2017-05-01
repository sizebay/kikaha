package kikaha.cloud.aws.lambda;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Typed;
import javax.inject.*;
import io.undertow.server.handlers.Cookie;
import kikaha.urouting.Reflection;
import kikaha.urouting.api.*;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Singleton
@SuppressWarnings( "unchecked" )
public class AmazonLambdaFunctionParameterReader {

	@Inject ConverterFactory converterFactory;
	@Inject @Typed( AmazonLambdaContextProducer.class )
	Iterable<AmazonLambdaContextProducer> availableProducers;

	Map<Class, AmazonLambdaContextProducer> contextProducers;

	@PostConstruct
	public void loadProducers(){
		contextProducers = loadAllProducers();
	}

	public <T> T getCookieParam( final AmazonLambdaRequest request, final String cookieParam, final Class<T> clazz) {
		final Cookie cookie = request.getCookies().get( cookieParam );
		if ( cookie == null )
			return null;
		return convert(cookie.getValue(), clazz);
	}

	public <T> T getHeaderParam( final AmazonLambdaRequest request, final String cookieParam, final Class<T> clazz) {
		final Map<String, String> headers = request.getHeaders();
		final String cookieValue = headers.get( cookieParam );
		if (cookieValue == null)
			return null;
		return convert(cookieValue, clazz);
	}

	public <T> T getPathParam( final AmazonLambdaRequest request, final String queryParam, final Class<T> clazz) {
		final String param = request.getPathParameters().get( queryParam );
		if (param == null)
			return null;
		return convert(param, clazz);
	}

	public <T> T getQueryParam( final AmazonLambdaRequest request, final String queryParam, final Class<T> clazz) {
		final String param = request.getQueryStringParameters().get( queryParam );
		if (param == null)
			return null;
		return convert(param, clazz);
	}

	public <T> T getContextParam( final AmazonLambdaRequest request, final Class<T> clazz ) {
		final AmazonLambdaContextProducer<T> producer = contextProducers.get(clazz);
		if ( producer == null )
			return null;
		return producer.produce( request );
	}

	private <T> T convert( String value, Class<T> targetClass ){
		try {
			return converterFactory.getConverterFor(targetClass).convert(value);
		} catch ( Throwable cause ) {
			throw new IllegalStateException( "Could not convert to " + targetClass + ". Value: " + value, cause );
		}
	}

	public <T> T getBody( AmazonLambdaRequest request, Class<T> userClass ) {
		return Jackson.fromJsonString( request.body, userClass );
	}

	private Map<Class, AmazonLambdaContextProducer> loadAllProducers() {
		final Map<Class, AmazonLambdaContextProducer> producers = new HashMap<>();
		for ( final AmazonLambdaContextProducer<?> producer : availableProducers ){
			final Class<?> forClazz = Reflection.getFirstGenericTypeFrom( producer, AmazonLambdaContextProducer.class );
			producers.put( forClazz, producer );
		}
		return producers;
	}
}
