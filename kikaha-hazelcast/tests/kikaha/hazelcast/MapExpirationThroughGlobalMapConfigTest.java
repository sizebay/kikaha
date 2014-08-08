package kikaha.hazelcast;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.concurrent.ExecutionException;

import lombok.val;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class MapExpirationThroughGlobalMapConfigTest {

	@Provided
	HazelcastInstance hazelcast;
	IMap<String, Integer> expirable;

	@Test
	public void ensureThatInsertedDataHaveExpired() throws InterruptedException, ExecutionException {
		expirable.put( "expirableEntry", 123 );
		assertThat( expirable.get( "expirableEntry" ), is( 123 ) );
		Wait.seconds( 4 );
		assertNull( expirable.get( "expirableEntry" ) );
	}

	@Before
	public void setup() throws ServiceProviderException {
		new ServiceProvider().provideOn( this );
		setExpirationConfigBeforeCreateTheMap();
		expirable = hazelcast.getMap( "expirable" );
	}

	void setExpirationConfigBeforeCreateTheMap() {
		val hazelcastConfig = hazelcast.getConfig();
		val expirableConfig = hazelcastConfig.getMapConfig( "expirable" );
		expirableConfig.setTimeToLiveSeconds( 3 );
	}

	@After
	public void shutdownHazelcast() {
		Hazelcast.shutdownAll();
	}
}
