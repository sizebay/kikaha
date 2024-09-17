package kikaha.uworkers.sqs;

import java.io.IOException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import kikaha.uworkers.api.*;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@RequiredArgsConstructor
public class SQSWorkerRef implements WorkerRef {

	final ObjectMapper mapper;
	final AmazonSQS sqs;
	final String queueUrl;

	@Override
	public <REQ> Response send( REQ request ) throws IOException {
		serializeAndSendObjectToQueue(request);
		return new SQSResponse();
	}

	@Override
	public Response send(Exchange exchange) throws IOException {
		serializeAndSendObjectToQueue(exchange.request());
		return exchange;
	}

	private void serializeAndSendObjectToQueue(Object request ) throws IOException {
		final String valueAsString = mapper.writeValueAsString(request);
		final SendMessageRequest sendMessageRequest = new SendMessageRequest( queueUrl, valueAsString );
		sqs.sendMessage(sendMessageRequest);
	}
}
