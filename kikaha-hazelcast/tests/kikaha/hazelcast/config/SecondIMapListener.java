package kikaha.hazelcast.config;

import com.hazelcast.core.IMap;

@SuppressWarnings( "rawtypes" )
public class SecondIMapListener implements HazelcastProducedDataListener<IMap> {

	public static volatile boolean called = false;

	@Override
	public void dataProduced( IMap data ) {
		called = true;
	}
}