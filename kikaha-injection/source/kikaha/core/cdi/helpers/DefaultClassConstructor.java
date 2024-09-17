package kikaha.core.cdi.helpers;

import java.lang.reflect.*;
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
	public <T> T instantiate(Class<T> clazz, ProviderContext providerContext) throws Exception {
		try {
			return clazz.newInstance();
		} catch ( IllegalAccessException cause ) {
			Constructor constructor = clazz.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			return (T)constructor.newInstance(new Object[0]);
		}
	}
}
