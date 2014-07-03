package io.skullabs.undertow.hazelcast;

import static org.junit.Assert.assertTrue;
import com.hazelcast.core.*;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import trip.spi.*;

public class HazelcastProducedDistributedDataStructuresTest {

	final ServiceProvider provider = new ServiceProvider();

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

	@Test
	public void ensureThatEveryParamSupposedToBeProvidedWasCorrectlyProvided() {
		assertTrue( IMap.class.isInstance( map ) );
		assertTrue( MultiMap.class.isInstance( multimap ) );
		assertTrue( IQueue.class.isInstance( queue ) );
		assertTrue( ISet.class.isInstance( set ) );
		assertTrue( IList.class.isInstance( list ) );
		assertTrue( ITopic.class.isInstance( topic ) );
		assertTrue( ILock.class.isInstance( lock ) );
	}

	@Before
	public void setup() throws ServiceProviderException {
		provider.provideOn( this );
	}
}
