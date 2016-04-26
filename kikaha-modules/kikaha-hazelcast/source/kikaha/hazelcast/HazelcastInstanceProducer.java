package kikaha.hazelcast;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import kikaha.config.Config;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HazelcastInstanceProducer {

	@Getter
	HazelcastInstance instance;

	@Inject
	Config config;

	@PostConstruct
	public void preloadInstance(){
		instance = createHazelcastInstance();
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
			return Hazelcast.newHazelcastInstance( cfg );
		// UNCHECKED: It should handle any exception thrown
		} catch ( final Exception cause ) {
		// CHECKED
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
		final com.hazelcast.config.Config config = new XmlConfigBuilder( configFile ).build();
		config.setProperty( "hazelcast.logging.type", "slf4j" );
		return config;
	}
}