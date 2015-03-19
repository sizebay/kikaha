package kikaha.hazelcast.config;

import com.hazelcast.config.Config;

public interface HazelcastConfigurationListener {

	void configCreated( Config instance ) throws Exception;

}
