package kikaha.hazelcast;

import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

@RunWith(KikahaRunner.class)
public class HazelcastProducedDistributedDataStructuresTest {

	@Inject
	@Named( "atomic-booleans" )
	IMap<Long, AtomicBoolean> map;

	@Inject
	@Named( "atomic-booleans" )
	IdGenerator idGenerator;

	@Test
	public void ensureThatEveryParamSupposedToBeProvidedWasCorrectlyProvided() {
		assertTrue( IMap.class.isInstance( map ) );
		assertTrue( IdGenerator.class.isInstance( idGenerator ) );
	}
}
