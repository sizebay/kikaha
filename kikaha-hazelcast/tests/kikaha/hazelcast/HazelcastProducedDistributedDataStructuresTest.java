package kikaha.hazelcast;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import kikaha.hazelcast.config.HazelcastTestCase;
import kikaha.hazelcast.config.IMapListener;
import kikaha.hazelcast.config.SecondIMapListener;

import org.junit.Test;

import trip.spi.Provided;

import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.ReplicatedMap;

public class HazelcastProducedDistributedDataStructuresTest extends HazelcastTestCase {

	@Provided
	@Source( "atomic-booleans" )
	IMap<Long, AtomicBoolean> map;

	@Provided
	@Source( "atomic-booleans" )
	MultiMap<Long, AtomicBoolean> multimap;

	@Provided
	@Source( "atomic-booleans" )
	IQueue<AtomicBoolean> queue;

	@Provided
	@Source( "atomic-booleans" )
	ISet<AtomicBoolean> set;

	@Provided
	@Source( "atomic-booleans" )
	IList<AtomicBoolean> list;

	@Provided
	@Source( "atomic-booleans" )
	ITopic<AtomicBoolean> topic;

	@Provided
	@Source( "atomic-booleans" )
	ILock lock;

	@Provided
	@Source( "executor-service" )
	IExecutorService executorService;
	
	@Provided
	@Source( "id-generator" )
	IdGenerator idGenerator;
	
	@Provided
	@Source( "replicated-map" )
	ReplicatedMap<Long,AtomicBoolean> replicatedMap;

	IMapListener listener = new IMapListener();

	SecondIMapListener secondListener = new SecondIMapListener();

	@Test
	public void ensureThatEveryParamSupposedToBeProvidedWasCorrectlyProvided() {
		assertTrue( IMap.class.isInstance( map ) );
		assertTrue( IMapListener.called );
		assertTrue( SecondIMapListener.called );
		assertTrue( MultiMap.class.isInstance( multimap ) );
		assertTrue( IQueue.class.isInstance( queue ) );
		assertTrue( ISet.class.isInstance( set ) );
		assertTrue( IList.class.isInstance( list ) );
		assertTrue( ITopic.class.isInstance( topic ) );
		assertTrue( ILock.class.isInstance( lock ) );
		assertTrue( IExecutorService.class.isInstance( executorService ) );
		assertTrue( ReplicatedMap.class.isInstance( replicatedMap ) );
		assertTrue( IdGenerator.class.isInstance( idGenerator ) );
	}
}
