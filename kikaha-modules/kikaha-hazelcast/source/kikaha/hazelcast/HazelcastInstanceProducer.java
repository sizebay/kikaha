package kikaha.hazelcast;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.*;
import javax.inject.*;
import java.io.InputStream;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.*;
import kikaha.config.Config;
import kikaha.core.SystemResource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class HazelcastInstanceProducer {

	@Getter
	HazelcastInstance instance;

	@Inject
	Config config;

	@Inject
	@Typed( HazelcastConfigurationListener.class )
	Iterable<HazelcastConfigurationListener> configurationListeners;

	@PostConstruct
	public void preloadInstance(){
		instance = createHazelcastInstance();
		Runtime.getRuntime().addShutdownHook( new Thread( instance::shutdown ) );
	}

	/**
	 * Produce a Hazelcast Instance. It will ensure that has only one instance
	 * of Hazelcast. You are always able to "inject"
	 * {@code HazelcastInstanceProducer} and call
	 * {@code createHazelcastInstance()} method manually to produce more
	 * Hazelcast instances.
	 *
	 * @return a HazelcastInstance
	 * @see HazelcastInstanceProducer#createHazelcastInstance()
	 */
	@Produces
	public HazelcastInstance produceHazelcastInstance() {
		return getInstance();
	}

	/**
	 * Creates a Hazelcast Instance. If the hazelcast mode, defined in
	 * {@code application.conf}, is set to "client", it will creates an instance
	 * that connects to a cluster node, otherwise, it will create an instance
	 * that behaves like a cluster node.<br>
	 * <br>
	 *
	 * <b>Note:</b> you are always able to provide your own {@link Config} or
	 * {@link ClientConfig} implementation through tRip producer.
	 *
	 * @return a HazelcastInstance
	 */
	public HazelcastInstance createHazelcastInstance() {
		try {
			final com.hazelcast.config.Config cfg = loadConfig();
			for ( HazelcastConfigurationListener listener : configurationListeners )
				listener.onConfigurationLoaded( cfg );
			return Hazelcast.newHazelcastInstance( cfg );
		// UNCHECKED: It should handle any exception thrown
		} catch ( final Exception cause ) {
		// CHECKED
			log.error( "Can't initialize Hazelcast", cause );
			throw new IllegalStateException( cause );
		}
	}

	/**
	 * Configure a connection as a cluster node.
	 *
	 * @return ClientConfig
	 */
	private com.hazelcast.config.Config loadConfig() throws Exception {
		final String configFile = config.getString("server.hazelcast.config");
		final InputStream inputStream = SystemResource.openFile( configFile );
		final com.hazelcast.config.Config config = new XmlConfigBuilder( inputStream ).build();
		config.setProperty( "hazelcast.logging.type", "slf4j" );
		return config;
	}
}