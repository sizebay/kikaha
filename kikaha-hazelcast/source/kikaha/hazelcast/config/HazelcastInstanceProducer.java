package kikaha.hazelcast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kikaha.core.api.conf.Configuration;
import kikaha.hazelcast.config.DistributableStructuresConfigParser;
import kikaha.hazelcast.config.HazelcastConfiguration;
import kikaha.hazelcast.config.HazelcastConfiguration.ClusterClientConfig;
import kikaha.hazelcast.config.MapConfiguration;
import kikaha.hazelcast.config.QueueConfiguration;
import lombok.Getter;
import lombok.val;
import lombok.extern.java.Log;
import trip.spi.Producer;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Log
@Singleton
public class HazelcastInstanceProducer {

	@Provided
	ServiceProvider provider;

	@Provided
	HazelcastConfiguration hazelcastConfig;

	@Provided
	HazelcastTripManagedContext managedContext;

	@Provided
	DistributableStructuresConfigParser parser;

	@Getter( lazy = true )
	private final HazelcastInstance instance = createHazelcastInstance();

	@Provided
	Configuration config;

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
	@Producer
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
			if ( hazelcastConfig.mode().equals( HazelcastConfiguration.MODE_CLIENT ) ) {
				final ClientConfig clientConfig = loadClientConfig();
				return HazelcastClient.newHazelcastClient( clientConfig );
			}
			final Config cfg = loadConfig();
			return Hazelcast.newHazelcastInstance( cfg );
		} catch ( Exception cause ) {
			throw new IllegalStateException( cause );
		}
	}

	ClientConfig loadClientConfig() {
		try {
			ClientConfig clientConfig = provider.load( ClientConfig.class );
			if ( clientConfig == null )
				clientConfig = createClientConfiguration();
			return clientConfig;
		} catch ( ServiceProviderException cause ) {
			log.warning( "Could not read Hazelcast Client Configuration: " + cause.getMessage() + ". Creating one manually." );
			return createClientConfiguration();
		}
	}

	/**
	 * Configure a connection as a client to a remote cluster node.
	 * 
	 * @return ClientConfig
	 */
	ClientConfig createClientConfiguration() {
		final ClientConfig clientConfig = new ClientConfig();
		final ClusterClientConfig clusterClient = hazelcastConfig.clusterClient();
		final GroupConfig groupConfig = clientConfig.getGroupConfig();
		configureGroupIdentification( clusterClient, groupConfig );
		for ( String address : clusterClient.addresses() )
			clientConfig.getNetworkConfig().addAddress( address );
		return clientConfig;
	}

	Config loadConfig() throws Exception {
		try {
			Config config = provider.load( Config.class );
			if ( config == null )
				config = createConfig();
			return config;
		} catch ( ServiceProviderException cause ) {
			log.warning( "Could not read Hazelcast Programmatically Configuration: " + cause.getMessage() + ". Creating one manually." );
			return createConfig();
		}
	}

	Config createConfig() throws Exception {
		final Config config = new XmlConfigBuilder().build();
		config.setManagedContext( managedContext );
		loadMapConfigs( config );
		loadQueueConfigs( config );

		final GroupConfig groupConfig = config.getGroupConfig();
		final ClusterClientConfig clusterClient = hazelcastConfig.clusterClient();

		if ( hazelcastConfig.overrideXmlConfig() )
			configureGroupIdentification( clusterClient, groupConfig );

		return config;
	}

	void loadMapConfigs( Config config ) throws Exception {
		val configs = parseConfigs(
				"server.hazelcast.data.maps", "server.hazelcast.data-defaults.map",
				MapConfiguration.class, MapConfig.class );
		val mapConfigs = new HashMap<String, MapConfig>();
		for ( val mapConf : configs )
			mapConfigs.put( mapConf.getName(), mapConf );
		config.setMapConfigs( mapConfigs );
	}

	void loadQueueConfigs( Config config ) throws Exception {
		val configs = parseConfigs(
				"server.hazelcast.data.queues", "server.hazelcast.data-defaults.queue",
				QueueConfiguration.class, QueueConfig.class );
		val mapConfigs = new HashMap<String, QueueConfig>();
		for ( val mapConf : configs )
			mapConfigs.put( mapConf.getName(), mapConf );
		config.setQueueConfigs( mapConfigs );
	}

	<T> List<T> parseConfigs( String itemsPath, String defaultsPath, Class<?> configClass, Class<T> returnType ) throws Exception {
		val defaultConf = config.config().getConfig( defaultsPath );
		val confs = new ArrayList<T>();
		for ( val entryConf : config.config().getConfigList( itemsPath ) ) {
			val newConfig = entryConf.withFallback( defaultConf );
			val parsedConf = parser.parse( newConfig, configClass, returnType );
			confs.add( parsedConf );
		}
		return confs;
	}

	/**
	 * Populates the group identification. On client configuration, it provides
	 * the data to connect to the remote cluster node. On cluster node
	 * configurations, it defines the expected parameters when a client tries to
	 * connect with it.
	 * 
	 * @param clusterClient
	 * @param groupConfig
	 */
	private void configureGroupIdentification(
			final ClusterClientConfig clusterClient, final GroupConfig groupConfig ) {
		if ( !isBlank( clusterClient.groupname() ) )
			groupConfig.setName( clusterClient.groupname() );
		else
			groupConfig.setName( config.applicationName() );
		if ( !isBlank( clusterClient.password() ) )
			groupConfig.setPassword( clusterClient.password() );
	}

	boolean isBlank( String string ) {
		return string == null || string.isEmpty();
	}
}