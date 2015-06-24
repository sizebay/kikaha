package kikaha.urouting;

import java.util.HashMap;

import kikaha.urouting.api.ExceptionHandler;
import trip.spi.Provided;
import trip.spi.ProvidedServices;
import trip.spi.ServiceProvider;
import trip.spi.Singleton;
import trip.spi.StartupListener;

@SuppressWarnings( { "rawtypes", "unchecked" } )
@Singleton( exposedAs=StartupListener.class )
public class RoutingMethodExceptionHandlerLoader implements StartupListener {

	@ProvidedServices(exposedAs=ExceptionHandler.class)
	Iterable<ExceptionHandler> availableHandlers;

	@Provided
	UnhandledExceptionHandler fallbackHandler;

	@Override
	public void onStartup(final ServiceProvider provider) {
		final HashMap<Class<?>, ExceptionHandler<?>> loadHandlers = loadHandlers();
		final RoutingMethodExceptionHandler exceptionHandler = new RoutingMethodExceptionHandler( loadHandlers, fallbackHandler );
		provider.providerFor( RoutingMethodExceptionHandler.class, exceptionHandler );
	}

	private HashMap<Class<?>, ExceptionHandler<?>> loadHandlers() {
		final HashMap<Class<?>, ExceptionHandler<?>> handlers = new HashMap<Class<?>, ExceptionHandler<?>>();
		for ( final ExceptionHandler handler : availableHandlers ) {
			final Class<?> throwableClass = getGenericClass( handler );
			handlers.put( throwableClass, handler );
		}
		return handlers;
	}

	private <T extends Throwable> Class<T> getGenericClass( final ExceptionHandler<T> handler ) {
		return (Class<T>)Reflection.getFirstGenericTypeFrom( handler, ExceptionHandler.class );
	}
}
