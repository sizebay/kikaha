package kikaha.hazelcast;

import com.hazelcast.core.*;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(KikahaRunner.class)
public class HazelcastProducedDistributedDataStructuresTest {

	@Inject
	@Named( "map-of-atomic-booleans" )
	IMap<Long, AtomicBoolean> map;

	@Inject
	@Named( "atomic-booleans" )
	MultiMap<Long, AtomicBoolean> multimap;

	@Inject
	@Named( "atomic-booleans" )
	IQueue<AtomicBoolean> queue;

	@Inject
	@Named( "atomic-booleans" )
	ISet<AtomicBoolean> set;

	@Inject
	@Named( "atomic-booleans" )
	IList<AtomicBoolean> list;

	@Inject
	@Named( "atomic-booleans" )
	ITopic<AtomicBoolean> topic;

	@Inject
	@Named( "atomic-booleans" )
	ILock lock;

	@Inject
	@Named( "executor-service" )
	IExecutorService executorService;

	@Inject
	@Named( "id-generator" )
	IdGenerator idGenerator;

	@Inject
	@Named( "replicated-map" )
	ReplicatedMap<Long,AtomicBoolean> replicatedMap;

	@Test
	public void ensureThatEveryParamSupposedToBeProvidedWasCorrectlyProvided() {
		assertTrue( IMap.class.isInstance( map ) );
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

	@Test
	public void ensureThatHaveCalledAllListeners(){
		assertEquals( "map-of-atomic-booleans", HazelcastProducedDataListenerEmptyImplementation.mapName );
		assertEquals( "map-of-atomic-booleans", HazelcastProducedDataListenerNonParametrizedEmptyImplementation.mapName );
	}
}
