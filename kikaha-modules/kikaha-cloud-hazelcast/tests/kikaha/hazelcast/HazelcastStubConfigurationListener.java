package kikaha.hazelcast;

import com.hazelcast.config.Config;

import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class HazelcastStubConfigurationListener implements HazelcastConfigurationListener {

	boolean configurationLoaded = false;

	@Override
	public void onConfigurationLoaded(Config config) {
		if ( config != null )
			configurationLoaded = true;
	}
}
