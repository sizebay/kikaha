package kikaha.hazelcast.config;

import static org.junit.Assert.assertNull;

import java.util.concurrent.CountDownLatch;

import kikaha.core.api.Source;
import lombok.SneakyThrows;

import org.junit.Test;

import trip.spi.Provided;
import trip.spi.ServiceProvider;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;

public class ConfigParserTest extends HazelcastTestCase {

	final CountDownLatch counterInvocation = new CountDownLatch( 1 );

	@Provided
	HazelcastInstance instance;

	@Provided
	@Source( "queue-configured-to-use-queue-store" )
	IQueue<String> queueThatUsesQueueStore;

	@Provided
	@Source( "map-configured-to-use-map-store" )
	IMap<String, String> mapThatUsesMapStore;

	@Test( timeout = 10000 )
	@SneakyThrows
	public void ensureThatCouldParseBasicTypesFromMapData() {
		assertNull( mapThatUsesMapStore.get( "unknown" ) );
		counterInvocation.await();
	}

	@Test( timeout = 10000 )
	@SneakyThrows
	public void ensureThatCouldParseBasicTypesFromQueueData() {
		queueThatUsesQueueStore.add( "unknown" );
		counterInvocation.await();
	}

	@Override
	protected void provideExtraDependencies( final ServiceProvider provider ) {
		provider.providerFor( CountDownLatch.class, counterInvocation );
	}
}
