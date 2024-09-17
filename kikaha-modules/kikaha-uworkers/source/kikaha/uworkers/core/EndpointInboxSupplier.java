package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;
import kikaha.uworkers.api.Worker;

import java.io.IOException;

/**
 * A simplified version from {@link EndpointInbox} designed for inbox which consumes a
 * single message per time.
 */
public interface EndpointInboxSupplier extends EndpointInbox {

    @Override
    default void receiveMessages(WorkerEndpointMessageListener listener) throws Exception {
        final Exchange exchange = getNextAvailableTask();
        notifyListener( listener, exchange );
    }

    default Exchange getNextAvailableTask() throws InterruptedException {
        try {
            return receiveMessage();
        } catch ( final EndpointInboxConsumerTimeoutException e ) {
            return new EmptyExchange();
        } catch ( final InterruptedException e ) {
            throw e;
        } catch ( final Throwable e ) {
            return new FailureExchange( e );
        }
    }

    /**
	 * Receives a message, waiting if necessary until an element becomes available.
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws EndpointInboxConsumerTimeoutException if it had elapsed the timeout
	 * 				to retrieve a message from the endpoint.
	 */
	@SuppressWarnings("unchecked")
	Exchange receiveMessage() throws InterruptedException, IOException, EndpointInboxConsumerTimeoutException;
}