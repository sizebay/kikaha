package kikaha.uworkers.sqs;

import java.io.IOException;
import java.util.List;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import kikaha.uworkers.api.Exchange;
import kikaha.uworkers.core.EndpointInboxSupplier;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@RequiredArgsConstructor
public class SQSEndpointInboxSupplier implements EndpointInboxSupplier {

	final ObjectMapper mapper;
	final AmazonSQS sqs;
	final String queueUrl;
	final int timeoutInSeconds;

	@Override
	public Exchange receiveMessage() throws InterruptedException, IOException {
		final ReceiveMessageRequest request = new ReceiveMessageRequest( queueUrl )
				.withMaxNumberOfMessages( 1 )
				.withWaitTimeSeconds(timeoutInSeconds);
		final ReceiveMessageResult result = sqs.receiveMessage(request);
		final List<Message> messages = result.getMessages();
		return new SQSExchange( mapper, sqs, messages.get(0) );
	}
}
