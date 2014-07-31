package kikaha.hazelcast;

import trip.spi.Producer;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.typesafe.config.Config;

@Singleton
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
