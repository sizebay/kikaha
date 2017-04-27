package kikaha.cloud.aws.lambda;

import javax.inject.*;
import java.util.Map;
import com.amazonaws.util.json.Jackson;
import io.undertow.server.handlers.Cookie;
import kikaha.urouting.api.ConverterFactory;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Singleton
@SuppressWarnings( "unchecked" )
public class AmazonLambdaFunctionParameterReader {

	@Inject ConverterFactory converterFactory;

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
}
