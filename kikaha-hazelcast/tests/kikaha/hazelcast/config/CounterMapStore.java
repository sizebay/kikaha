package kikaha.hazelcast.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import trip.spi.Provided;

import com.hazelcast.core.MapStore;

public class CounterMapStore implements MapStore<String, String> {

	@Provided
	CountDownLatch counterOfMapStoreInvocation;

	@Override
	public String load( String key ) {
		counterOfMapStoreInvocation.countDown();
		return key;
	}

	@Override
	public Map<String, String> loadAll( Collection<String> keys ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> loadAllKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void store( String key, String value ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeAll( Map<String, String> map ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete( String key ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll( Collection<String> keys ) {
		// TODO Auto-generated method stub

	}
}
