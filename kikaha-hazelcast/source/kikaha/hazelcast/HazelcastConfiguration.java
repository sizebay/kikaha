package kikaha.hazelcast;

import com.typesafe.config.Config;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents the entries defined in application.conf file that belongs to
 * hazelcast behavior configuration in Undertow Standalone Extension.
 */
@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class HazelcastConfiguration {

	public static final String MODE_CLIENT = "client";
	public static final String MODE_CLUSTER_NODE = "cluster-node";

	final Config config;

	@Getter( lazy = true )
	private final Boolean overrideXmlConfig = config().getBoolean( "undertow.hazelcast.override-xml-config" );

	@Getter( lazy = true )
	private final Boolean sessionManagementEnabled = config().getBoolean( "undertow.hazelcast.session-management-enabled" );

	@Getter( lazy = true )
	private final String mode = config().getString( "undertow.hazelcast.mode" );

	@Getter( lazy = true )
	private final ClusterClientConfig clusterClient = new ClusterClientConfig( config() );

	@Getter
	@Accessors( fluent = true )
	@RequiredArgsConstructor
	static class ClusterClientConfig {

		final Config config;

		@Getter( lazy = true )
		private final String groupname = config().getString( "undertow.hazelcast.group.name" );

		@Getter( lazy = true )
		private final String password = config().getString( "undertow.hazelcast.group.password" );

		@Getter( lazy = true )
		private final List<String> addresses = config().getStringList( "undertow.hazelcast.address" );
	}
}
