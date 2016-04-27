package kikaha.hazelcast;

import kikaha.urouting.Reflection;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
@SuppressWarnings( { "rawtypes" } )
public class HazelcastProducedDataListenerFactory {
	
	final List<HazelcastProducedDataListener<?>> emptyListenerList = Collections.emptyList();

	@Inject
	@Typed(HazelcastProducedDataListener.class)
	Iterable<HazelcastProducedDataListener> listeners;

	Map<Class, List<HazelcastProducedDataListener<?>>> cache;

	@PostConstruct
	public void readCache() {
		cache = new HashMap<>();
		for ( final HazelcastProducedDataListener listener : listeners ){
			final Class key = Reflection.getFirstGenericTypeFrom(listener, HazelcastProducedDataListener.class);
			final List<HazelcastProducedDataListener<?>> list = getCacheFor(key);
			list.add(listener);
		}
	}

	public List<HazelcastProducedDataListener<?>> getListenerFor( Class<?> clazz ) {
		return cache.getOrDefault( clazz, emptyListenerList );
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