package kikaha.hazelcast;

import com.hazelcast.core.IMap;

import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class HazelcastProducedDataListenerEmptyImplementation
	implements  HazelcastProducedDataListener<IMap<String,Long>> {

	static String mapName;

	@Override
	public void dataProduced(IMap<String, Long> data) {
		mapName = data.getName();
	}
}
