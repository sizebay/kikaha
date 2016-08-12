package kikaha.core.cdi.helpers;

/**
 * The default constructor of classes.
 */
public class DefaultClassConstructor implements CustomClassConstructor {

	@Override
	public boolean isAbleToInstantiate( Class<?> clazz ) {
		return true;
	}

	@Override
	public <T> T instantiate(Class<T> clazz) throws IllegalAccessException, InstantiationException {
		return clazz.newInstance();
	}
}
