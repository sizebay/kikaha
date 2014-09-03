package kikaha.hazelcast;

import kikaha.core.impl.conf.DefaultConfiguration;

import com.typesafe.config.Config;

import trip.spi.Producer;

public class TestProducers {

	@Producer
	public Config produceDefaultConfiguration() {
		return DefaultConfiguration.loadDefaultConfig();
	}
}
