package kikaha.cloud.aws;

import com.amazonaws.ClientConfiguration;

/**
 * An interface to programmatically configure AWS' {@link ClientConfiguration}.
 */
public interface AWSClientProgrammaticConfiguration {

	void configure( ClientConfiguration clientConfiguration );
}
