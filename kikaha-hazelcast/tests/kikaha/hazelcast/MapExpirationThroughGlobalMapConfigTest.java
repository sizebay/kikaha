package kikaha.hazelcast;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.concurrent.ExecutionException;

import kikaha.hazelcast.config.HazelcastTestCase;
import lombok.val;

import org.junit.Test;

import trip.spi.Provided;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class MapExpirationThroughGlobalMapConfigTest extends HazelcastTestCase {

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

	@Override
	public void afterProvideDependencies() {
		setExpirationConfigBeforeCreateTheMap();
		expirable = hazelcast.getMap( "expirable" );
	}

	void setExpirationConfigBeforeCreateTheMap() {
		val hazelcastConfig = hazelcast.getConfig();
		val expirableConfig = hazelcastConfig.getMapConfig( "expirable" );
		expirableConfig.setTimeToLiveSeconds( 3 );
	}
}
