package kikaha.urouting;

import java.util.Map;

import kikaha.urouting.api.ExceptionHandler;
import kikaha.urouting.api.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoutingMethodExceptionHandler {

	final Map<Class<?>, ExceptionHandler<?>> handlers;
	final ExceptionHandler<Throwable> fallbackHandler;

	public Response handle( final Throwable cause ) {
		ExceptionHandler<Throwable> handler = retrieveHandlerFor( cause.getClass() );

		if ( handler == null )
			handler = fallbackHandler;

		return handler.handle( cause );
	}

	@SuppressWarnings( "unchecked" )
	private ExceptionHandler<Throwable> retrieveHandlerFor( Class<?> clazz ) {
		ExceptionHandler<Throwable> handler = null;
		while ( !Object.class.equals( clazz ) && handler == null ) {
			handler = (ExceptionHandler<Throwable>)handlers.get( clazz );
			clazz = clazz.getSuperclass();
		}
		return handler;
	}
}
