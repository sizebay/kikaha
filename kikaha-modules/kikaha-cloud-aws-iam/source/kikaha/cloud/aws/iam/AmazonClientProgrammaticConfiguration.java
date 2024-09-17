package kikaha.cloud.aws.iam;

import com.amazonaws.ClientConfiguration;

/**
 * An interface to programmatically configure AWS' {@link ClientConfiguration}.
 */
public interface AmazonClientProgrammaticConfiguration {

	void configure( ClientConfiguration clientConfiguration );
}
