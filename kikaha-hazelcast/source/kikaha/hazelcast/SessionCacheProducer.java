package kikaha.hazelcast;

import lombok.val;
import trip.spi.Producer;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Singleton
public class SessionCacheProducer {

	public final static String SESSION_CACHE = "session-cache";

	@Provided
	HazelcastInstance hazelcast;

	@Provided
	HazelcastConfiguration configuration;

	@Producer( name = SESSION_CACHE )
	public IMap<String, Session> produceSessionCache() {
		val hazelcastConfig = hazelcast.getConfig();
		val expirableConfig = hazelcastConfig.getMapConfig( SESSION_CACHE );
		expirableConfig.setTimeToLiveSeconds( configuration.sessionTimeToLive() );
		return hazelcast.getMap( SESSION_CACHE );
	}
}