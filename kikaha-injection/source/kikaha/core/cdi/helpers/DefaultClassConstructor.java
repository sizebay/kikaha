package kikaha.core.cdi.helpers;

import kikaha.core.cdi.ProviderContext;

/**
 * The default constructor of classes.
 */
public class DefaultClassConstructor implements CustomClassConstructor {

	@Override
	public boolean isAbleToInstantiate(Class<?> clazz, ProviderContext providerContext) {
		return true;
	}

	@Override
	public <T> T instantiate(Class<T> clazz, ProviderContext providerContext) throws IllegalAccessException, InstantiationException {
		return clazz.newInstance();
	}
}
