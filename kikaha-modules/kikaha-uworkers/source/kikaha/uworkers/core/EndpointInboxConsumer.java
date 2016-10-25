package kikaha.uworkers.core;

import java.io.IOException;
import kikaha.uworkers.api.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A background task that consumes messages from endpoints and forward it to {@link Worker}s.
 */
@Slf4j
@RequiredArgsConstructor
public class EndpointInboxConsumer implements Runnable {

	final EndpointInboxSupplier inbox;
	final WorkerEndpointMessageListener listener;
	final String name;

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
		} catch ( InterruptedException | IOException e ) {
			log.error( "Could not receive a message", e );
			return null;
		}
	}
}
