package io.skullabs.undertow.hazelcast;

import com.typesafe.config.Config;
import trip.spi.*;

@Service
public class HazelcastConfigurationProducer {

	@Provided
	Config config;
	HazelcastConfiguration hazelcastConfig;

	@Producer
	public HazelcastConfiguration createHazelcastConfig() {
		if ( hazelcastConfig == null ) {
			compatibilizeHazelcastConfigurationWithTypesafeModel();
			hazelcastConfig = new HazelcastConfiguration( config );
		}
		return hazelcastConfig;
	}

	private void compatibilizeHazelcastConfigurationWithTypesafeModel() {
		new TypesafeConfigurationCompatibilization( config ).compatibilize();
	}
}
