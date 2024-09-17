package kikaha.hazelcast;

public interface HazelcastProducedDataListener<T> {

	void dataProduced( T data );
}