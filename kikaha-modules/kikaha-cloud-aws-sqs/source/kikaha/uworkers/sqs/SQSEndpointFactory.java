package kikaha.uworkers.sqs;

import javax.inject.*;
import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import kikaha.config.Config;
import kikaha.uworkers.api.WorkerRef;
import kikaha.uworkers.core.*;

/**
 *
 */
@Singleton
public class SQSEndpointFactory implements PollingEndpointFactory {

	@Inject AmazonSQS amazonSQS;

	@Override
	public EndpointInbox createSupplier( EndpointConfig endpoint ) {
		final Config endpointConfig = endpoint.getConfig();
		final ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();
		return new SQSEndpointInboxSupplier( objectMapper, amazonSQS,
			endpointConfig.getString( "url" ),
			endpointConfig.getInteger( "wait-time-seconds", 60 ),
            endpointConfig.getInteger( "max-number-of-messages", 10 ));
	}

	@Override
	public WorkerRef createWorkerRef( EndpointConfig endpoint ) {
		final Config endpointConfig = endpoint.getConfig();
		final ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();
		return new SQSWorkerRef(objectMapper, amazonSQS,
				endpointConfig.getString( "url" ));
	}
}
