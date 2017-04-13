package kikaha.hazelcast;

import com.hazelcast.config.Config;

/**
 *
 */
public interface HazelcastConfigurationListener {

	void onConfigurationLoaded( Config config );
}
