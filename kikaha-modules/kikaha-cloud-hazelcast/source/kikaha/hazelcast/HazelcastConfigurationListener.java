package kikaha.hazelcast;

import java.io.IOException;
import com.hazelcast.config.Config;

/**
 *
 */
public interface HazelcastConfigurationListener {

	void onConfigurationLoaded( Config config ) throws IOException;
}
