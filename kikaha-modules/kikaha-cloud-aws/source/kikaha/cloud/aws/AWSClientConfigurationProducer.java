package kikaha.cloud.aws;

import java.util.Collection;
import javax.enterprise.inject.*;
import javax.inject.*;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.ec2.AmazonEC2Client;
import lombok.Getter;

/**
 * A producer of {@link ClientConfiguration}.
 */
@Singleton
public class AWSClientConfigurationProducer {

	@Inject @Typed(AWSClientProgrammaticConfiguration.class)
	Collection<AWSClientProgrammaticConfiguration> listeners;

	@Getter(lazy = true)
	private final ClientConfiguration clientConfiguration = createClientConfiguration();

	private ClientConfiguration createClientConfiguration() {
		final ClientConfiguration configuration = new ClientConfiguration();
		listeners.forEach( c->c.configure( configuration ) );
		return configuration;
	}

	@Produces
	public ClientConfiguration produceClientConfiguration(){
		return getClientConfiguration();
	}
}
