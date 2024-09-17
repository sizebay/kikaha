package kikaha.cloud.aws.iam;

import java.util.Collection;
import javax.enterprise.inject.*;
import javax.inject.*;
import com.amazonaws.ClientConfiguration;
import lombok.Getter;

/**
 * A producer of {@link ClientConfiguration}.
 */
@Singleton
public class AmazonClientConfigurationProducer {

	@Inject @Typed(AmazonClientProgrammaticConfiguration.class)
	Collection<AmazonClientProgrammaticConfiguration> listeners;

	@Getter(lazy = true)
	private final ClientConfiguration clientConfiguration = createClientConfiguration();

	private ClientConfiguration createClientConfiguration() {
		final ClientConfiguration configuration = new ClientConfiguration();
		configuration.setConnectionTimeout(6000);
		configuration.setRequestTimeout(30000);
		listeners.forEach( c->c.configure( configuration ) );
		return configuration;
	}

	@Produces
	public ClientConfiguration produceClientConfiguration(){
		return getClientConfiguration();
	}
}
