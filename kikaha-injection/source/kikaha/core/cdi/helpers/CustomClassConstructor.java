package kikaha.core.cdi.helpers;

import kikaha.core.cdi.ProviderContext;

/**
 * Instantiate classes that are supposed to be managed by the CDI Context.
 */
public interface CustomClassConstructor {

	/**
	 * Check whether this class constructor is able to construct a class or not.
	 *
	 * @param clazz
	 * @param providerContext
	 * @return
	 */
	boolean isAbleToInstantiate(Class<?> clazz, ProviderContext providerContext);

	/**
	 * Instantiate the given {@code clazz}.
	 *
	 * @param <T>
	 * @param clazz
	 * @param providerContext
	 * @return
	 */
	<T> T instantiate(Class<T> clazz, ProviderContext providerContext) throws Exception;
}
