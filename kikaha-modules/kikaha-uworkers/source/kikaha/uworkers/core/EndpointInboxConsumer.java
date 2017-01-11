package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;
import kikaha.uworkers.api.Worker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A background task that consumes messages from endpoints and send it to {@link Worker}s.
 */
@Slf4j
@RequiredArgsConstructor
public class EndpointInboxConsumer implements Runnable {

	final EndpointInboxSupplier inbox;
	final WorkerEndpointMessageListener listener;
	final String endpointURL;

	@Override
	public void run() {
		Exchange exchange;
		while ( (exchange = getNextAvailableTask()) != null ) {
			try {
				listener.onMessage(exchange);
			} catch ( Throwable c ) {
				exchange.reply( c );
			}
		}
	}

	private Exchange getNextAvailableTask(){
		try {
			return inbox.receiveMessage();
		} catch ( InterruptedException e ) {
			log.debug( "Could not receive a message", e );
		} catch ( Throwable e ) {
			log.error( "Could not receive a message", e );
			return new FailureExchange( e );
		}
		return null;
	}
}
