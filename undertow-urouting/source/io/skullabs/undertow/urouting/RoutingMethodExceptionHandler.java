package io.skullabs.undertow.urouting;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import trip.spi.*;
import urouting.api.*;

@Service
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
		final Iterable<ExceptionHandler> iterableHandlers = provider.loadAll( ExceptionHandler.class );
		for ( ExceptionHandler handler : iterableHandlers ) {
			Class<?> throwableClass = getGenericClass( handler );
			handlers.put( throwableClass, handler );
		}
		return handlers;
	}

	@SuppressWarnings( { "unchecked" } )
	protected <T extends Throwable> Class<T> getGenericClass( ExceptionHandler<T> handler ) {
		final Type[] genericInterfaces = handler.getClass().getGenericInterfaces();
		for ( Type genericInterface : genericInterfaces )
			if ( ParameterizedType.class.isInstance( genericInterface )
					&& ExceptionHandler.class.equals( ( (ParameterizedType)genericInterface ).getRawType() ) )
				return (Class<T>)( (ParameterizedType)genericInterface ).getActualTypeArguments()[0];
		return null;
	}
}
