package kikaha.uworkers.core;

import java.util.concurrent.atomic.AtomicBoolean;
import kikaha.uworkers.api.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A background task that consumes messages from endpoints and send it to {@link Worker}s.
 */
@Slf4j
@RequiredArgsConstructor
public class EndpointInboxConsumer implements Runnable {

	final AtomicBoolean isShutdown;
	final EndpointInbox inbox;
	final WorkerEndpointMessageListener listener;
	final String name;

	@Override
	public void run() {
		try {
			while (!isShutdown.get()) {
				try {
					inbox.receiveMessages( listener );
				} catch (InterruptedException e) {
					log.debug("Consumer failed to get next message because it was interrupted...", e);
				} catch (Throwable e){
					log.error( "Worker consumer "+name+" failed", e );
				}
			}
		} finally {
			log.debug( "Worker Consumer "+name+" finished! Shutdown = " + isShutdown.get() );
		}
	}
}
