package kikaha.hazelcast;

import static kikaha.core.util.Lang.convert;
import java.io.IOException;
import javax.inject.*;
import com.hazelcast.config.*;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;

/**
 *
 */
@Singleton
public class AwsDiscoveryStrategyConfiguration implements HazelcastConfigurationListener {

	@Inject ApplicationData strategy;
	@Inject kikaha.config.Config kikahaConfig;

	@Override
	public void onConfigurationLoaded(Config config) throws IOException {
		if ( kikahaConfig.getBoolean( "server.hazelcast.use-cluster-members-only", false ) ) {
			final JoinConfig join = config.getNetworkConfig().getJoin();
			join.getAwsConfig().setEnabled( false );
			join.getMulticastConfig().setEnabled( false );

			final TcpIpConfig tcpIpConfig = join.getTcpIpConfig().setEnabled(true);
			convert(strategy.getSiblingNodesOnTheCluster(), ApplicationData::getLocalAddress)
				.forEach( tcpIpConfig::addMember);
		}
	}
}
