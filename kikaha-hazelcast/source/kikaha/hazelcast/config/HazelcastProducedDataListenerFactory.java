package kikaha.hazelcast.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kikaha.urouting.Reflection;
import trip.spi.ProvidedServices;
import trip.spi.Singleton;

@Singleton
@SuppressWarnings( { "rawtypes" } )
public class HazelcastProducedDataListenerFactory {
	
	final List<HazelcastProducedDataListener<?>> emptyListenerList = Collections.emptyList();

	@ProvidedServices(exposedAs=HazelcastProducedDataListener.class)
	Iterable<HazelcastProducedDataListener> listeners;

	Map<Class, List<HazelcastProducedDataListener<?>>> cache;

	public List<HazelcastProducedDataListener<?>> getListenerFor( Class<?> clazz ) {
		return getCache().getOrDefault( clazz, emptyListenerList );
	}

	private Map<Class, List<HazelcastProducedDataListener<?>>> getCache() {
		if ( cache == null )
			synchronized (this) {
				if ( cache == null )
					readCache();
			}
		return cache;
	}

	private void readCache() {
		cache = new HashMap<>();
		for ( final HazelcastProducedDataListener listener : listeners ){
			final Class key = Reflection.getFirstGenericTypeFrom( listener, HazelcastProducedDataListener.class );
			final List<HazelcastProducedDataListener<?>> list = getCacheFor( key );
			list.add( listener );
		}
	}

	private List<HazelcastProducedDataListener<?>> getCacheFor( final Class key ) {
		List<HazelcastProducedDataListener<?>> list = cache.get( key );
		if ( list == null ) {
			list = new ArrayList<>();
			cache.put( key, list );
		}
		return list;
	}
}
