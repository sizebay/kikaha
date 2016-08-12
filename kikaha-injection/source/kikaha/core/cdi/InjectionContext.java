package kikaha.core.cdi;

import static java.lang.reflect.Modifier.*;
import static java.util.Arrays.asList;
import java.util.*;
import kikaha.core.cdi.helpers.*;
import kikaha.core.cdi.helpers.ServiceLoader;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@RequiredArgsConstructor
public class InjectionContext {

	final Map<Class<?>, Object> cache = new HashMap<>();
	final Map<Class<?>, ProvidableClass<?>> providableClassCache = new HashMap<>();
	final Map<Class<?>, Iterable<Class<?>>> implementedClasses = new HashMap<>();

	Iterable<CustomClassConstructor> customClassConstructors = asList( new DefaultClassConstructor() );
	InjectableDataExtractor qualifierExtractor;

	@SuppressWarnings("unchecked")
	public <T> Iterable<T> instantiate( Iterable<Class<T>> classes ){
		final List<T> list = new TinyList<>();
		synchronized ( cache ) {
			for ( final Class<T> clazz : classes ) {
				T object = (T)cache.get( clazz );
				if ( object == null )
					cache.put( clazz, object = instantiate( clazz ) );
				list.add( object );
			}
		}
		return list;
	}

	public <T> T instantiate( Class<T> clazz ) {
		try {
			for ( CustomClassConstructor constructor : customClassConstructors )
				if ( constructor.isAbleToInstantiate( clazz ) )
					return constructor.instantiate( clazz );
		} catch ( final IllegalAccessException | InstantiationException cause ) {
			if ( !isAbstract( clazz.getModifiers() ) && !isInterface( clazz.getModifiers() ))
				log.debug("Can't instantiate " + clazz + ": " + cause.getMessage());
		}
		return null;
	}

	public ProvidableClass<?> retrieveProvidableClass( final Class<?> targetClazz ) {
		ProvidableClass<?> providableClass = providableClassCache.get( targetClazz );
		if ( providableClass == null )
			synchronized ( providableClassCache ) {
				providableClass = providableClassCache.get( targetClazz );
				if ( providableClass == null ) {
					providableClass = ProvidableClass.wrap( qualifierExtractor, targetClazz );
					providableClassCache.put( targetClazz, providableClass );
				}
			}
		return providableClass;
	}

	public <T> List<Class<T>> loadClassesImplementing( final Class<T> interfaceClazz ) {
		List<Class<T>> implementations = (List)implementedClasses.get( interfaceClazz );
		if ( implementations == null )
			synchronized ( implementedClasses ) {
				implementations = (List)implementedClasses.get( interfaceClazz );
				if ( implementations == null ) {
					implementations = ServiceLoader.loadImplementationsFor( interfaceClazz );
					implementedClasses.put( (Class)interfaceClazz, (Iterable)implementations );
				}
			}
		return implementations;
	}
}