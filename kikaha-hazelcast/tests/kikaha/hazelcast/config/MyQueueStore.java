package kikaha.hazelcast.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import trip.spi.Provided;
import trip.spi.Singleton;

import com.hazelcast.core.QueueStore;

@Singleton
public class MyQueueStore implements QueueStore<String> {

	@Provided
	CountDownLatch store;

	@Override
	public void store( Long key, String value ) {
		store.countDown();
	}

	@Override
	public void storeAll( Map<Long, String> map ) {

	}

	@Override
	public void delete( Long key ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll( Collection<Long> keys ) {
		// TODO Auto-generated method stub

	}

	@Override
	public String load( Long key ) {
		return null;
	}

	@Override
	public Map<Long, String> loadAll( Collection<Long> keys ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> loadAllKeys() {
		return null;
	}

}
