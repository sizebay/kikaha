package kikaha.hazelcast.mapstore;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import lombok.extern.java.Log;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.hazelcast.core.MapStore;

@Log
@Singleton
public class MyInjectableMapStore implements MapStore<String, Object>, Serializable {

	private static final long serialVersionUID = -4432429890495577870L;

	@Provided
	Store store;

	public MyInjectableMapStore() {
		log.info( "STORE" );
	}

	@Override
	public Object load( String key ) {
		log.info( key );
		return null;
	}

	@Override
	public Map<String, Object> loadAll( Collection<String> keys ) {
		log.info( keys.toString() );
		return null;
	}

	@Override
	public Set<String> loadAllKeys() {
		return store.loadAllKeys();
	}

	@Override
	public void store( String key, Object value ) {
		log.info( key );
	}

	@Override
	public void storeAll( Map<String, Object> map ) {
		log.info( map.toString() );
	}

	@Override
	public void delete( String key ) {
		log.info( key );
	}

	@Override
	public void deleteAll( Collection<String> keys ) {}
}