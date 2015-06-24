package kikaha.urouting;

import java.util.Map;

import kikaha.urouting.api.ExceptionHandler;
import kikaha.urouting.api.Response;
import kikaha.urouting.api.UnhandledException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoutingMethodExceptionHandler {

	final Map<Class<?>, ExceptionHandler<?>> handlers;
	final UnhandledExceptionHandler fallbackHandler;

	@SuppressWarnings( "unchecked" )
	public <T extends Throwable> Response handle( final T cause ) {
		Class<?> clazz = cause.getClass();
		while ( !Object.class.equals( clazz ) ) {
			final ExceptionHandler<T> handler = (ExceptionHandler<T>)handlers.get( clazz );
			if ( handler != null )
				return handler.handle( cause );
			clazz = clazz.getSuperclass();
		}
		return fallbackHandler.handle( new UnhandledException( cause ) );
	}
}
