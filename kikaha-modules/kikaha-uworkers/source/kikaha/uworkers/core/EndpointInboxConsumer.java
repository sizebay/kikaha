package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;
import kikaha.uworkers.api.Worker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A background task that consumes messages from endpoints and send it to {@link Worker}s.
 */
@Slf4j
@RequiredArgsConstructor
public class EndpointInboxConsumer implements Runnable {

	final AtomicBoolean isShutdown;
	final EndpointInboxSupplier inbox;
	final WorkerEndpointMessageListener listener;
	final String name;

	@Override
	public void run() {
		while ( !isShutdown.get() ) {
			try {
				consumeNextMessage();
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	private void consumeNextMessage() throws InterruptedException {
		final Exchange exchange = getNextAvailableTask();
		try {
			if ( !EmptyExchange.class.isInstance( exchange ) )
				listener.onMessage(exchange);
		} catch ( final Throwable c ) {
			exchange.reply(c);
		}
	}

	private Exchange getNextAvailableTask() throws InterruptedException {
		try {
			return inbox.receiveMessage();
		} catch ( final EndpointInboxConsumerTimeoutException e ) {
			return new EmptyExchange();
		} catch ( final InterruptedException e ) {
			log.debug( "Endpoint finished", e );
			throw e;
		} catch ( final Throwable e ) {
			log.debug( "Could not receive a message", e );
			return new FailureExchange( e );
		}
	}
}
