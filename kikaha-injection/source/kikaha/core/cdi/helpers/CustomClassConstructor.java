package kikaha.core.cdi.helpers;

/**
 * Instantiate classes that are supposed to be managed by the CDI Context.
 */
public interface CustomClassConstructor {

	/**
	 * Check whether this class constructor is able to construct a class or not.
	 *
	 * @param clazz
	 * @return
	 */
	boolean isAbleToInstantiate( Class<?> clazz );

	/**
	 * Instantiate the given {@code clazz}.
	 *
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	<T> T instantiate( Class<T> clazz ) throws IllegalAccessException, InstantiationException;
}
