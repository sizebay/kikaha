package kikaha.hazelcast.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.hazelcast.core.QueueStore;

public class MyQueueStore implements QueueStore<String> {

	@Override
	public void store( Long key, String value ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeAll( Map<Long, String> map ) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, String> loadAll( Collection<Long> keys ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> loadAllKeys() {
		// TODO Auto-generated method stub
		return null;
	}

}
