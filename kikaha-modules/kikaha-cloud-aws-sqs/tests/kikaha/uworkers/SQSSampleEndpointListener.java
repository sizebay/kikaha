package kikaha.uworkers;

import javax.inject.Singleton;
import kikaha.uworkers.api.Worker;
import lombok.EqualsAndHashCode;

/**
 *
 */
@Singleton
public class SQSSampleEndpointListener {

	volatile Message message;

	@Worker("sample-msg")
	public void receiveMessage( Message message ){
		this.message = message;
	}

	@EqualsAndHashCode
	public static class Message {
		final long id = System.nanoTime();
	}
}
