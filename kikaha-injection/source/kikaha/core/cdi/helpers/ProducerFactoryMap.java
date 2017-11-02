package kikaha.core.cdi.helpers;

import java.lang.reflect.*;
import java.util.*;
import kikaha.core.cdi.*;
import kikaha.core.cdi.helpers.filter.AnyObject;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings( "rawtypes" )
@Slf4j
public class ProducerFactoryMap {

	final Map<Class<?>, List<Class<ProducerFactory>>> producerImplementationClasses = new HashMap<>();
	final Map<Class<?>, List<ProducerFactory<?>>> map = new HashMap<>();

	public static ProducerFactoryMap from( final Iterable<Class<ProducerFactory>> iterable ) {
	    log.debug( "Loading all implementations of ProducerFactory interface..." );
		final ProducerFactoryMap providers = new ProducerFactoryMap();
		for ( final Class<ProducerFactory> provider : iterable ) {
			final Class<?> clazz = getGenericClassFrom( provider );
			log.debug( "  > " + provider.getCanonicalName() + " -> " + clazz.getCanonicalName() );
			providers.memorizeProviderForClazz(provider, clazz);
		}
		return providers;
	}

	private static Class<?> getGenericClassFrom( Class<? extends ProducerFactory> clazz ) {
		final Type[] types = clazz.getGenericInterfaces();
		for ( final Type type : types )
			if ( ( (ParameterizedType)type ).getRawType().equals( ProducerFactory.class ) )
				return (Class<?>)( (ParameterizedType)type ).getActualTypeArguments()[0];
		return null;
	}

	private void memorizeProviderForClazz( final Class<ProducerFactory> provider, final Class<?> clazz ) {
		List<Class<ProducerFactory>> iterable = producerImplementationClasses.get( clazz );
		if ( iterable == null ) {
			iterable = new TinyList<>();
			producerImplementationClasses.put( clazz, iterable );
		}
		iterable.add( provider );
	}

	public ProducerFactory<?> get( final Class<?> clazz, final DefaultCDI.DependencyInjector injector ) {
		final List<ProducerFactory<?>> list = getAll( clazz, injector );
		if ( list == null || list.isEmpty() )
			return null;
		return list.get(0);
	}

	private List<ProducerFactory<?>> getAll( final Class<?> clazz, DefaultCDI.DependencyInjector injector ) {
		List<ProducerFactory<?>> list = map.get( clazz );
		if ( list == null )
			synchronized ( map ) {
				list = map.get( clazz );
				if ( list == null ) {
					list = loadAll( clazz, injector );
					map.put( clazz, list );
				}
			}
		return list;
	}

	private List<ProducerFactory<?>> loadAll( Class<?> clazz, DefaultCDI.DependencyInjector injector ) {
		final List<ProducerFactory<?>> list = new TinyList<>();
		final List<Class<ProducerFactory>> factories = producerImplementationClasses.get( clazz );
		if ( factories != null )
			for ( final Class<ProducerFactory> factoryClass : factories )
				list.add( injector.load( factoryClass, AnyObject.instance(), EmptyProviderContext.INSTANCE ) );
		return list;
	}

	public void memorizeProviderForClazz( final ProducerFactory<?> provider, final Class<?> clazz ) {
		synchronized ( map ) {
			List<ProducerFactory<?>> list = map.get( clazz );
			if ( list == null ) {
				list = new TinyList<>();
				map.put( clazz, list );
			}
			list.add( provider );
		}
	}
}
