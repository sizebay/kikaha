package kikaha.core.cdi.helpers;

import java.lang.reflect.*;
import java.util.*;
import javax.inject.*;
import kikaha.core.cdi.*;

/**
 *
 */
@Singleton
public class InjectableClassConstructor implements CustomClassConstructor {

	@Inject CDI cdi;

	@Override
	public boolean isAbleToInstantiate(Class<?> clazz, ProviderContext providerContext) {
		final Optional<Constructor<?>> constructor = getInjectableConstructor(clazz);
		return constructor.isPresent();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T instantiate(Class<T> clazz, ProviderContext providerContext) throws IllegalAccessException, InstantiationException {
		final Constructor<?> constructor = getInjectableConstructor(clazz).get();
		constructor.setAccessible(true);

		try {
			final Object[] parameters = getParametersForConstructor(constructor);
			return (T) constructor.newInstance( parameters );
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	private Optional<Constructor<?>> getInjectableConstructor(Class<?> clazz ) {
		while( !Object.class.equals(clazz)) {
			for (Constructor<?> constructor : clazz.getDeclaredConstructors())
				if ( constructor.isAnnotationPresent(Inject.class) )
					return Optional.of( constructor );
			clazz = clazz.getSuperclass();
		}
		return Optional.empty();
	}

	private Object[] getParametersForConstructor( final Constructor<?> constructor ){
		final Class<?>[] parameterTypes = constructor.getParameterTypes();
		final Object[] parameters = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++)
			parameters[i] = cdi.load(parameterTypes[i]);
		return parameters;
	}
}
