package io.skullabs.undertow.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.skullabs.undertow.hazelcast.HazelcastConfiguration.ClusterClientConfig;
import trip.spi.*;

@Service
public class HazelcastInstanceProducer {

	@Provided
	HazelcastConfiguration hazelcastConfig;
	HazelcastInstance instance;

	@Producer
	public HazelcastInstance produceHazelcastInstance() {
		if ( instance == null )
			instance = createHazelcastInstance();
		return instance;
	}

	HazelcastInstance createHazelcastInstance() {
		if ( hazelcastConfig.mode().equals( HazelcastConfiguration.MODE_CLIENT ) ) {
			final ClientConfig clientConfig = createClientConfiguration();
			return HazelcastClient.newHazelcastClient( clientConfig );
		}
		final Config cfg = new Config();
		return Hazelcast.newHazelcastInstance( cfg );
	}

	ClientConfig createClientConfiguration() {
		final ClientConfig clientConfig = new ClientConfig();
		final ClusterClientConfig clusterClient = hazelcastConfig.clusterClient();
		if ( !isBlank( clusterClient.username() ) )
			clientConfig.getGroupConfig()
					.setName( clusterClient.username() )
					.setPassword( clusterClient.password() );
		for ( String address : clusterClient.addresses() )
			clientConfig.getNetworkConfig().addAddress( address );
		return clientConfig;
	}

	boolean isBlank( String string ) {
		return string == null || string.isEmpty();
	}
}