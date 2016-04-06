package kikaha.core;

import kikaha.config.Config;
import kikaha.config.ConfigLoader;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

/**
 * Make the default configuration widely available.
 */
@Singleton
public class KikahaConfigurationProducer {

	final Config config = ConfigLoader.loadDefaults();

	@Produces
	public Config produceAConfiguration(){
		return config;
	}
}
