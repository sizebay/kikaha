package io.skullabs.undertow.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.skullabs.undertow.hazelcast.HazelcastConfiguration.ClusterClientConfig;
import io.skullabs.undertow.standalone.api.Configuration;
import lombok.extern.java.Log;
import trip.spi.*;

@Log
@Service
public class HazelcastInstanceProducer {

	@Provided
	ServiceProvider provider;

	@Provided
	HazelcastConfiguration hazelcastConfig;
	HazelcastInstance instance;

	@Provided
	Configuration undertowConfiguration;

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
		if ( instance == null )
			instance = createHazelcastInstance();
		return instance;
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
		if ( hazelcastConfig.mode().equals( HazelcastConfiguration.MODE_CLIENT ) ) {
			final ClientConfig clientConfig = loadClientConfig();
			return HazelcastClient.newHazelcastClient( clientConfig );
		}
		final Config cfg = loadConfig();
		return Hazelcast.newHazelcastInstance( cfg );
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

	Config loadConfig() {
		try {
			Config config = provider.load( Config.class );
			if ( config == null )
				config = createConfig();
			return config;
		} catch ( ServiceProviderException cause ) {
			log.warning( "Could not read Hazelcast Configuration: " + cause.getMessage() + ". Creating one manually." );
			return createConfig();
		}
	}

	private Config createConfig() {
		final Config config = new XmlConfigBuilder().build();
		final GroupConfig groupConfig = config.getGroupConfig();
		final ClusterClientConfig clusterClient = hazelcastConfig.clusterClient();

		if ( hazelcastConfig.overrideXmlConfig() )
			configureGroupIdentification( clusterClient, groupConfig );

		return config;
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
			groupConfig.setName( undertowConfiguration.applicationName() );
		if ( !isBlank( clusterClient.password() ) )
			groupConfig.setPassword( clusterClient.password() );
	}

	boolean isBlank( String string ) {
		return string == null || string.isEmpty();
	}
}