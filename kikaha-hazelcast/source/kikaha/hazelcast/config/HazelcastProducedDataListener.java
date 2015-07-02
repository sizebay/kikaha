package kikaha.hazelcast.config;

public interface HazelcastProducedDataListener<T> {

	void dataProduced( T data );
}
