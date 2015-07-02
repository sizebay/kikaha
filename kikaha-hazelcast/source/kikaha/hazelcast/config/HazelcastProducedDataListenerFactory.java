package kikaha.hazelcast.config;

import java.util.HashMap;
import java.util.Map;

import kikaha.urouting.Reflection;
import trip.spi.ProvidedServices;
import trip.spi.Singleton;

@Singleton
@SuppressWarnings({"rawtypes","unchecked"})
public class HazelcastProducedDataListenerFactory {
	
	@ProvidedServices(exposedAs=HazelcastProducedDataListener.class)
	Iterable<HazelcastProducedDataListener> listeners;

	Map<Class, HazelcastProducedDataListener> cache;
	
	public <T> HazelcastProducedDataListener<T> getListenerFor( Class<T> clazz ){
		return getCache().get(clazz);
	}

	Map<Class, HazelcastProducedDataListener> getCache(){
		if ( cache == null )
			synchronized (this) {
				if ( cache == null )
					readCache();
			}
		return cache;
	}

	private void readCache() {
		cache = new HashMap<>();
		for ( HazelcastProducedDataListener listener : listeners ){
			Class key = Reflection.getFirstGenericTypeFrom( listener, HazelcastProducedDataListener.class );
			cache.put(key, listener);
		}
	}
}
