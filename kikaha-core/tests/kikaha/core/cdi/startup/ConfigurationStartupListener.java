package kikaha.core.cdi.startup;

import kikaha.core.cdi.ServiceProvider;
import kikaha.core.cdi.StartupListener;

public class ConfigurationStartupListener implements StartupListener {

	public static final String EXPECTED_CONFIG = "I was Injected";

	@Override
	public void onStartup( final ServiceProvider provider ) {
		provider.providerFor( Configuration.class, new Configuration( EXPECTED_CONFIG ) );
	}
}
