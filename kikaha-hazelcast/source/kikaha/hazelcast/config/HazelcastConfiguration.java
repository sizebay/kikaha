package kikaha.hazelcast.config;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.typesafe.config.Config;

/**
 * Represents the entries defined in application.conf file that belongs to
 * hazelcast behavior configuration in Kikaha.
 */
@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class HazelcastConfiguration {

	public static final String MODE_CLIENT = "client";
	public static final String MODE_CLUSTER_NODE = "cluster-node";

	final Config config;

	@Getter( lazy = true )
	private final Boolean overrideXmlConfig = config().getBoolean( "server.hazelcast.override-xml-config" );

	@Getter( lazy = true )
	private final Boolean sessionManagementEnabled = config().getBoolean( "server.hazelcast.session-management.enabled" );

	@Getter( lazy = true )
	private final Integer sessionTimeToLive = config().getInt( "server.hazelcast.session-management.time-to-live" );

	@Getter( lazy = true )
	private final String mode = config().getString( "server.hazelcast.mode" );

	@Getter( lazy = true )
	private final ClusterClientConfig clusterClient = new ClusterClientConfig( config() );

	@Getter
	@Accessors( fluent = true )
	@RequiredArgsConstructor
	public static class ClusterClientConfig {

		final Config config;

		@Getter( lazy = true )
		private final String groupname = config().getString( "server.hazelcast.group.name" );

		@Getter( lazy = true )
		private final String password = config().getString( "server.hazelcast.group.password" );

		@Getter( lazy = true )
		private final List<String> addresses = config().getStringList( "server.hazelcast.address" );
	}
}
