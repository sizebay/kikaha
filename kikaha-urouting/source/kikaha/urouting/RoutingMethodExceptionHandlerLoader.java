package kikaha.urouting;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import kikaha.core.api.conf.Configuration;
import kikaha.urouting.api.ExceptionHandler;
import kikaha.urouting.api.UnhandledException;
import trip.spi.Producer;
import trip.spi.Provided;
import trip.spi.ProvidedServices;
import trip.spi.ServiceProvider;
import trip.spi.Singleton;

@SuppressWarnings( { "rawtypes", "unchecked" } )
@Singleton
public class RoutingMethodExceptionHandlerLoader {

	@ProvidedServices(exposedAs=ExceptionHandler.class)
	Iterable<ExceptionHandler> availableHandlers;

	@Provided
	Configuration kikahaConf;

	@Provided
	ServiceProvider provider;

	RoutingMethodExceptionHandler exceptionHandler;
	ExceptionHandler<Throwable> fallbackHandler;

	@PostConstruct
	public void loadData() {
		fallbackHandler = loadFallbackHandler();
		final HashMap<Class<?>, ExceptionHandler<?>> loadHandlers = loadHandlers();
		exceptionHandler = new RoutingMethodExceptionHandler( loadHandlers, fallbackHandler );
	}

	private ExceptionHandler<Throwable> loadFallbackHandler() {
		try {
			final String className = kikahaConf.config().getString( "server.exception-handler" );
			final Class<?> handlerClass = Class.forName( className );
			return (ExceptionHandler<Throwable>)provider.load( handlerClass );
		} catch ( final ClassNotFoundException e ) {
			throw new UnhandledException( e );
		}
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

	@Producer
	public RoutingMethodExceptionHandler produceExceptionHandler() {
		return exceptionHandler;
	}
}
