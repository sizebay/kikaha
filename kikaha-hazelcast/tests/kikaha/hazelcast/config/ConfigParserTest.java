package kikaha.hazelcast.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Before;
import org.junit.Test;

import trip.spi.Provided;
import trip.spi.ServiceProvider;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig.EvictionPolicy;
import com.hazelcast.config.MaxSizeConfig.MaxSizePolicy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapStore;
import com.typesafe.config.Config;

public class ConfigParserTest {

	final CountDownLatch counterOfMapStoreInvocation = new CountDownLatch( 1 );

	@Provided
	HazelcastInstance instance;

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

	@Before
	@SneakyThrows
	public void injectDependencies() {
		val config = DefaultConfiguration.loadDefaultConfiguration();

		val provider = new ServiceProvider();
		provider.providerFor( CountDownLatch.class, counterOfMapStoreInvocation );
		provider.providerFor( Config.class, config.config() );
		provider.providerFor( Configuration.class, config );

		provider.provideOn( this );
	}
}
