package io.skullabs.undertow.hazelcast;

import com.typesafe.config.Config;
import io.skullabs.undertow.standalone.DefaultConfiguration;
import trip.spi.Producer;

public class TestProducers {

	@Producer
	public Config produceDefaultConfiguration() {
		return DefaultConfiguration.loadDefaultConfig();
	}
}
