package io.skullabs.undertow.hazelcast;

import com.typesafe.config.Config;
import trip.spi.*;

@Service
public class HazelcastConfigProducer {

	@Provided
	Config config;
	HazelcastConfig hazelcastConfig;

	@Producer
	public HazelcastConfig createHazelcastConfig() {
		if ( hazelcastConfig == null ) {
			compatibilizeHazelcastConfigurationWithTypesafeModel();
			hazelcastConfig = new HazelcastConfig( config );
		}
		return hazelcastConfig;
	}

	private void compatibilizeHazelcastConfigurationWithTypesafeModel() {
		new TypesafeConfigurationCompatibilization( config ).compatibilize();
	}
}
