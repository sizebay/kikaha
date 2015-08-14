package kikaha.core;

import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProvider;

public class TestCase {

	protected Configuration configuration = DefaultConfiguration.loadDefaultConfiguration();
	protected ServiceProvider provider = createServiceProvider();

	protected ServiceProvider createServiceProvider() {
		ServiceProvider provider = new DefaultServiceProvider();
		provider.providerFor( Configuration.class, configuration );
		return provider;
	}
}
