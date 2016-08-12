package kikaha.core.cdi;

import kikaha.core.cdi.helpers.EmptyProviderContext;
import kikaha.core.cdi.helpers.filter.AnyObject;
import kikaha.core.cdi.helpers.filter.Condition;
import kikaha.core.cdi.helpers.filter.Filter;

/**
 * It manages singleton and stateless instances, inject data into beans
 * and create new instance of classes that could benefits with the injection mechanism.
 */
public interface ServiceProvider {

	/**
	 * Load a service represented by the argument {@code serviceClazz}.
	 * If no service was found, it will try to instantiate the class and
	 * inject data.
	 *
	 * @param serviceClazz - the service interface(or class) representation
	 * @return - the loaded or created service.
	 */
	default <T> T load( final Class<T> serviceClazz ) {
		return load( serviceClazz, AnyObject.instance() );
	}

	/**
	 * Load a service represented by the argument {@code serviceClazz}.
	 * If no service was found, it will try to instantiate the class and
	 * inject data.
	 *
	 * @param serviceClazz - the service interface(or class) representation
	 * @param condition - a filter condition
	 * @return - the loaded or created service.
	 */
	default <T> T load( final Class<T> serviceClazz, final Condition<T> condition ) {
		return load( serviceClazz, condition, EmptyProviderContext.INSTANCE );
	}

	default <T> T load( final Class<T> serviceClazz, final ProviderContext context ) {
		return load( serviceClazz, AnyObject.instance(), context );
	}

	<T> T load(Class<T> serviceClazz, Condition<T> condition, ProviderContext context);

	/**
	 * Load all services represented by the argument {@code serviceClazz}.
	 * If no service was found, it will try to instantiate the class,
	 * inject data and return an {@link Iterable} with this instance.
	 *
	 * @param serviceClazz - the service interface(or class) representation
	 * @param condition - a filter condition
	 * @return - all loaded or created services.
	 */
	default <T> Iterable<T> loadAll( final Class<T> serviceClazz, final Condition<T> condition ) {
		return Filter.filter( loadAll( serviceClazz ), condition );
	}

	/**
	 * Load all services represented by the argument {@code serviceClazz}.
	 * If no service was found, it will try to instantiate the class,
	 * inject data and return an {@link Iterable} with this instance.
	 *
	 * @param serviceClazz - the service interface(or class) representation
	 * @return - all loaded or created services.
	 */
	<T> Iterable<T> loadAll(Class<T> serviceClazz);

	/**
	 * Defines a factory to be invoked every time a service represented with
	 * {@code serviceClazz} is requested.
	 *
	 * @param serviceClazz - the service interface(or class) representation
	 * @param provider - the producer implementation
	 */
	<T> void providerFor(Class<T> serviceClazz, ProducerFactory<T> provider);

	/**
	 * Defines a factory to be invoked every time a service represented with
	 * {@code serviceClazz} is requested.
	 *
	 * @param serviceClazz - the service interface(or class) representation
	 * @param object - the service implementation
	 */
	<T> void providerFor(Class<T> serviceClazz, T object);

	/**
	 * Inject data into all objects present in {@code iterable}.
	 *
	 * @param iterable - the set of objects that will receive "injectable" services.
	 */
	<T> void provideOn(Iterable<T> iterable);

	/**
	 * Inject data into {@code object}.
	 *
	 * @param object - the objects that will receive "injectable" services.
	 */
	void provideOn(Object object);
}
