package kikaha.hazelcast.config;

import lombok.Getter;
import trip.spi.Producer;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.typesafe.config.Config;

@Singleton
public class HazelcastConfigurationProducer {

	@Getter( lazy = true )
	private final HazelcastConfiguration hazelcastConfig = createConfiguration();

	@Provided
	Config config;

	private HazelcastConfiguration createConfiguration() {
		new TypesafeConfigurationCompatibilization( config ).compatibilize();
		return new HazelcastConfiguration( config );
	}

	@Producer
	public HazelcastConfiguration createHazelcastConfig() {
		return getHazelcastConfig();
	}
}
