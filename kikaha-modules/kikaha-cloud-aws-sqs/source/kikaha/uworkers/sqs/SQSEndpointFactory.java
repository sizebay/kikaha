package kikaha.uworkers.sqs;

import javax.inject.*;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.amazonaws.services.sqs.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import kikaha.cloud.aws.iam.AmazonConfigurationProducer;
import kikaha.cloud.aws.iam.AmazonConfigurationProducer.AmazonWebServiceConfiguration;
import kikaha.config.Config;
import kikaha.uworkers.api.WorkerRef;
import kikaha.uworkers.core.*;

/**
 *
 */
@Singleton
public class SQSEndpointFactory implements EndpointFactory {

	@Inject ClientConfiguration clientConfiguration;
	@Inject AmazonConfigurationProducer configurationProducer;

	@Override
	public EndpointInboxSupplier createSupplier( EndpointConfig endpoint ) {
		final Config endpointConfig = endpoint.getConfig();
		final ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();
		return new SQSEndpointInboxSupplier( objectMapper,
			sqsClient( endpointConfig.getString( "sqs-configuration", "sqs" ) ),
			endpointConfig.getString( "url" ),
			endpointConfig.getInteger( "timeout", 1 ));
	}

	@Override
	public WorkerRef createWorkerRef( EndpointConfig endpoint ) {
		final Config endpointConfig = endpoint.getConfig();
		final ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();
		return new SQSWorkerRef(objectMapper,
				sqsClient( endpointConfig.getString( "sqs-configuration", "sqs" ) ),
				endpointConfig.getString( "url" ));
	}

	AmazonSQS sqsClient( String alias ){
		final AmazonWebServiceConfiguration amazonConfiguration = configurationProducer.configForService(alias);
		return AmazonSQSClient.builder()
			.withCredentials( amazonConfiguration.getIamPolicy() )
			.withRegion( amazonConfiguration.getRegion() )
			.withClientConfiguration( clientConfiguration )
				.build();
	}
}
