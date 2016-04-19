package kikaha.urouting;

import kikaha.core.modules.websocket.WebSocketSession;
import kikaha.urouting.api.ConversionException;
import kikaha.urouting.api.ConverterFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Provides data to a routing method.
 */
@Singleton
public class WebSocketDataProvider {

	@Inject
	ConverterFactory converterFactory;

	/**
	 * Get a header parameter from request converted to the {@code <T>} type as
	 * defined by {@code clazz} argument.
	 *
	 * @param session
	 * @param headerParam
	 * @param clazz
	 * @return
	 * @throws ConversionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T getHeaderParam(final WebSocketSession session, final String headerParam, final Class<T> clazz )
			throws ConversionException, InstantiationException, IllegalAccessException {
		final String value = first( session.requestHeaders().get( headerParam ) );
		if ( value == null )
			return null;
		return converterFactory.getConverterFor( clazz ).convert( value );
	}

	/**
	 * Get a path parameter from request converted to the {@code <T>} type as
	 * defined by {@code clazz} argument.
	 *
	 * @param session
	 * @param pathParam
	 * @param clazz
	 * @return
	 * @throws ConversionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T getPathParam( final WebSocketSession session, final String pathParam, final Class<T> clazz )
			throws ConversionException, InstantiationException, IllegalAccessException {
		final String value = session.requestParameters().get( pathParam );
		if ( value == null )
			return null;
		return converterFactory.getConverterFor( clazz ).convert( value );
	}

	<T> T first( final List<T> values ) {
		if ( values != null && values.size() > 0 )
			return values.get( 0 );
		return null;
	}
}
