package kikaha.urouting;

import java.util.HashMap;
import java.util.Map;

import kikaha.urouting.api.ExceptionHandler;
import kikaha.urouting.api.Response;
import kikaha.urouting.api.UnhandledException;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

@Singleton
public class RoutingMethodExceptionHandler {

	@Provided
	ServiceProvider provider;
	Map<Class<?>, ExceptionHandler<?>> handlers;

	@SuppressWarnings( "unchecked" )
	public <T extends Throwable> Response handle( T cause ) {
		cause.printStackTrace();
		Class<?> clazz = cause.getClass();
		while ( !Object.class.equals( clazz ) ) {
			ExceptionHandler<T> handler = (ExceptionHandler<T>)handlers().get( clazz );
			if ( handler != null )
				return handler.handle( cause );
			clazz = clazz.getSuperclass();
		}
		throw new UnhandledException( cause );
	}

	protected Map<Class<?>, ExceptionHandler<?>> handlers() {
		try {
			if ( handlers == null )
				handlers = loadHandlers();
			return handlers;
		} catch ( ServiceProviderException cause ) {
			throw new IllegalStateException( cause );
		}
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	protected HashMap<Class<?>, ExceptionHandler<?>> loadHandlers() throws ServiceProviderException {
		final HashMap<Class<?>, ExceptionHandler<?>> handlers = new HashMap<Class<?>, ExceptionHandler<?>>();
		final Iterable<ExceptionHandler> iterableHandlers = provider.loadSingletons( ExceptionHandler.class );
		for ( ExceptionHandler handler : iterableHandlers ) {
			Class<?> throwableClass = getGenericClass( handler );
			handlers.put( throwableClass, handler );
		}
		return handlers;
	}

	@SuppressWarnings( { "unchecked" } )
	protected <T extends Throwable> Class<T> getGenericClass( ExceptionHandler<T> handler ) {
		return (Class<T>)Reflection.getFirstGenericTypeFrom( handler, ExceptionHandler.class );
	}
}
