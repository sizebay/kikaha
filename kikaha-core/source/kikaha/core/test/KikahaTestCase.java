package kikaha.core.test;

import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProvider;

public abstract class KikahaTestCase {

	public static Configuration configuration = DefaultConfiguration.loadDefaultConfiguration();
	public static ServiceProvider provider = createServiceProvider();

	static ServiceProvider createServiceProvider() {
		final ServiceProvider provider = new DefaultServiceProvider();
		provider.providerFor( Configuration.class, configuration );
		return provider;
	}

	static void injectInto( Object injetable ) {
		provider.provideOn( injetable );
	}
}
