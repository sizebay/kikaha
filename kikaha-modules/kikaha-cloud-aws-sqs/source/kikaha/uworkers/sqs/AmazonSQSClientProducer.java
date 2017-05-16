package kikaha.uworkers.sqs;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.sqs.*;
import kikaha.cloud.aws.iam.AmazonConfigurationProducer.AmazonWebServiceConfiguration;
import lombok.Getter;

/**
 *
 */
@Singleton
public class AmazonSQSClientProducer {

	@Inject @Named( "sqs" )
	AmazonWebServiceConfiguration amazonConfiguration;

	@Inject ClientConfiguration clientConfiguration;

	@Getter(lazy = true)
	private final AmazonSQS amazonSQS = createAmazonSQSClient();

	@Produces AmazonSQS produceAmazonSQS(){
		return getAmazonSQS();
	}

	AmazonSQS createAmazonSQSClient() {
		return AmazonSQSClient.builder()
			.withCredentials(amazonConfiguration.getIamPolicy())
			.withRegion(amazonConfiguration.getRegion())
			.withClientConfiguration(clientConfiguration)
				.build();
	}
}
