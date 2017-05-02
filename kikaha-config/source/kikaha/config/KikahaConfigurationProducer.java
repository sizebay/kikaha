package kikaha.config;

import java.util.Collection;
import javax.enterprise.inject.*;
import javax.inject.*;
import lombok.Getter;

/**
 * Make the default configuration widely available.
 */
@Singleton
public class KikahaConfigurationProducer {

	final Config defaultConfiguration = ConfigLoader.loadDefaults();

	@Inject @Typed( ConfigEnrichment.class )
	Collection<ConfigEnrichment> listOfEnrichment;

	@Getter( lazy = true )
	private final Config config = loadConfiguration();

	private Config loadConfiguration() {
		Config config = defaultConfiguration;
		for ( final ConfigEnrichment enrichment : listOfEnrichment )
			config = enrichment.enrich( config );
		return config;
	}

	@Produces
	public Config produceAConfiguration(){
		return getConfig();
	}
}
