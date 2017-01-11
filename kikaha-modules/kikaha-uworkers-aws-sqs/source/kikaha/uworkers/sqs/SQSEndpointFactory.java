package kikaha.uworkers.sqs;

import javax.inject.*;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import kikaha.config.Config;
import kikaha.uworkers.api.WorkerRef;
import kikaha.uworkers.core.*;

/**
 *
 */
@Singleton
public class SQSEndpointFactory implements EndpointFactory {

	@Inject
    MicroWorkersContext microWorkersContext;
	@Inject AWSCredentials credentials;
	@Inject ClientConfiguration clientConfiguration;

	@Override
	public boolean canHandleEndpoint(String endpointName) {
		return microWorkersContext.getEndpointConfig( endpointName ) != null;
	}

	@Override
	public EndpointInboxSupplier createSupplier(String endpointName) {
		final Config endpointConfig = microWorkersContext.getEndpointConfig(endpointName);
		final ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();
		final AmazonSQSClient client = new AmazonSQSClient(credentials, clientConfiguration);
		return new SQSEndpointInboxSupplier( objectMapper, client,
			endpointConfig.getString( "url" ),
			endpointConfig.getInteger( "timeout", 1 ));
	}

	@Override
	public WorkerRef createWorkerRef(String endpointName) {
		final Config endpointConfig = microWorkersContext.getEndpointConfig(endpointName);
		final ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();
		final AmazonSQSClient client = new AmazonSQSClient(credentials, clientConfiguration);
		return new SQSWorkerRef(objectMapper, client, endpointConfig.getString( "url" ));
	}
}
