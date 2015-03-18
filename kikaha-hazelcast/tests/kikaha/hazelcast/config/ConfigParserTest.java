package kikaha.hazelcast.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import kikaha.hazelcast.Source;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Test;

import trip.spi.Provided;
import trip.spi.ServiceProvider;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig.EvictionPolicy;
import com.hazelcast.config.MaxSizeConfig.MaxSizePolicy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.MapStore;

public class ConfigParserTest extends HazelcastTestCase {

	final CountDownLatch counterOfMapStoreInvocation = new CountDownLatch( 1 );

	@Provided
	HazelcastInstance instance;

	@Provided
	@Source( "unconfigured-queue" )
	IQueue<String> unconfiguredQueue;

	@Test
	@SneakyThrows
	@SuppressWarnings( "unchecked" )
	public void ensureThatCouldParseBasicTypesFromMapData() {
		val mapConfig = instance.getConfig().getMapConfig( "sample" );
		assertNotNull( mapConfig );
		assertEquals( "sample", mapConfig.getName() );
		assertEquals( InMemoryFormat.BINARY, mapConfig.getInMemoryFormat() );
		assertEquals( 0, mapConfig.getBackupCount() );
		assertEquals( 1, mapConfig.getAsyncBackupCount() );
		assertTrue( mapConfig.isReadBackupData() );
		assertEquals( 0, mapConfig.getTimeToLiveSeconds() );
		assertEquals( 0, mapConfig.getMaxIdleSeconds() );
		assertEquals( EvictionPolicy.LRU, mapConfig.getEvictionPolicy() );

		val maxSizeConfig = mapConfig.getMaxSizeConfig();
		assertEquals( MaxSizePolicy.PER_NODE, maxSizeConfig.getMaxSizePolicy() );
		assertEquals( 5000, maxSizeConfig.getSize() );

		assertEquals( 25, mapConfig.getEvictionPercentage() );

		val nearCacheConfig = mapConfig.getNearCacheConfig();
		assertTrue( nearCacheConfig.isInvalidateOnChange() );

		val mapStoreConfig = mapConfig.getMapStoreConfig();
		val mapStore = (MapStore<String, String>)mapStoreConfig.getImplementation();
		assertEquals( "blah", mapStore.load( "blah" ) );
		counterOfMapStoreInvocation.await();

		val indexes = mapConfig.getMapIndexConfigs();
		assertNotNull( indexes );
		assertEquals( "id", indexes.get( 0 ).getAttribute() );
		assertEquals( true, indexes.get( 0 ).isOrdered() );
		assertEquals( "name", indexes.get( 1 ).getAttribute() );
		assertEquals( false, indexes.get( 1 ).isOrdered() );

		val entryListeners = mapConfig.getEntryListenerConfigs();
		assertNotNull( entryListeners );
		assertEquals( false, entryListeners.get( 0 ).isLocal() );
		assertEquals( true, entryListeners.get( 0 ).isIncludeValue() );
		assertTrue( entryListeners.get( 0 ).getImplementation().getClass().equals( MyEntryListener.class ) );
	}

	@Test
	public void ensureThatCouldParseBasicTypesFromQueueData() {
		val queueConfig = instance.getConfig().getQueueConfig( "sample" );
		assertNotNull( queueConfig );
		assertEquals( "sample", queueConfig.getName() );
		assertEquals( Integer.MAX_VALUE, queueConfig.getMaxSize() );
		assertEquals( 0, queueConfig.getAsyncBackupCount() );
		assertEquals( -1, queueConfig.getEmptyQueueTtl() );
		val listenerConfig = queueConfig.getItemListenerConfigs().get( 0 );
		assertTrue( listenerConfig.getImplementation().getClass().equals( MyQueueItemListener.class ) );
		val storeConfig = queueConfig.getQueueStoreConfig();
		assertTrue( storeConfig.getStoreImplementation().getClass().equals( MyQueueStore.class ) );

	}

	protected void provideExtraDependencies( final ServiceProvider provider ) {
		provider.providerFor( CountDownLatch.class, counterOfMapStoreInvocation );
	}
}
