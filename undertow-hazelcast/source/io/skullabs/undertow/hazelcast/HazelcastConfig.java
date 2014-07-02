package io.skullabs.undertow.hazelcast;

import com.typesafe.config.Config;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class HazelcastConfig {

	public static final String MODE_CLIENT = "client";
	public static final String MODE_CLUSTER_NODE = "cluster-node";

	final Config config;

	@Getter( lazy = true )
	private final Boolean sessionManagementEnabled = config().getBoolean( "undertow.hazelcast.session-management-enabled" );

	@Getter( lazy = true )
	private final String mode = config().getString( "undertow.hazelcast.mode" );

	final ClusterClientConfig clusterClient = new ClusterClientConfig( config() );

	@Getter
	@Accessors( fluent = true )
	@RequiredArgsConstructor
	static class ClusterClientConfig {

		final Config config;

		@Getter( lazy = true )
		private final String username = config().getString( "undertow.hazelcast.mode" );

		@Getter( lazy = true )
		private final String password = config().getString( "undertow.hazelcast.mode" );

		@Getter( lazy = true )
		private final List<String> addresses = config().getStringList( "undertow.hazelcast.mode" );

	}
}
