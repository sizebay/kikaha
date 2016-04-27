package kikaha.hazelcast;

import com.hazelcast.core.IMap;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class HazelcastProducedDataListenerNonParametrizedEmptyImplementation
	implements  HazelcastProducedDataListener<IMap> {

	static String mapName;

	@Override
	public void dataProduced(IMap data) {
		mapName = data.getName();
	}
}
