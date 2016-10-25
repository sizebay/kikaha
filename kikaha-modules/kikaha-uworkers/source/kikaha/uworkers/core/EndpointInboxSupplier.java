package kikaha.uworkers.core;

import java.io.IOException;
import kikaha.uworkers.api.*;

/**
 * A supplier responsible for the receiving messages that will be forwarded to
 * a given {@link Worker}. This implementation represents the Worker's Inbox.
 * Developers are encouraged to implement this interface in order to allow workers
 * consumer messages from custom external brokers like RabbitMQ, Kafka and ActiveMQ.
 */
public interface EndpointInboxSupplier {

	/**
	 * Receives a message, waiting if necessary until an element becomes available.
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	Exchange receiveMessage() throws InterruptedException, IOException;
}