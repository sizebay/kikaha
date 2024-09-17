package kikaha.uworkers.api;

import java.io.IOException;
import lombok.RequiredArgsConstructor;

/**
 * Represents a communication between the sender and the receiver of a message.
 */
public interface Exchange extends Response {

	/**
	 * Returns the object sent by the sender.
	 * @return
	 */
	<REQ> REQ request();

	/**
	 * Returns the object sent by the sender.
	 * @param targetClass
	 * @return
	 */
	<REQ> REQ requestAs( Class<REQ> targetClass ) throws IOException;

	/**
	 * Send a response to the sender.
	 *
	 * @param response
	 * @return this Exchange
	 * @throws UnsupportedOperationException if this Exchange does not permit
	 *      send message back to the sender.
	 * @see Exchange#isReplySupported()
	 */
	<RESP> Exchange reply(RESP response);

	/**
	 * Send an error response to the sender.
	 *
	 * @param error
	 * @return this Exchange
	 * @throws UnsupportedOperationException if this Exchange does not permit
	 *      send message back to the sender.
	 * @see Exchange#isReplySupported()
	 */
	Exchange reply(Throwable error);

	/**
	 * Returns {@code true} if this Exchange message permits send message
	 * back to the sender.
	 *
	 * @return
	 */
	default boolean isReplySupported(){
		return true;
	}

	/**
	 * Send an empty object as response to the send. It is useful to
	 * make the send aware that the job the Worker was supposed to do have finished.
	 *
	 * @return
	 */
	default Exchange acknowledge() {
		return reply( new TimeStamp() );
	}

	/**
	 * Forward a message to a given {@link WorkerRef}. Developers are encouraged to use
	 * this method instead of {@link WorkerRef#send} in order to keep the code cleaner.
	 *
	 * @param request
	 * @param <NEW_REQ>
	 * @return
	 */
	default <NEW_REQ> Forwardable forward( NEW_REQ request ){
		return new Forwardable( request, this );
	}

	/**
	 * A helper class that holds an {@code exchange} to send a {@code request} message to a {@link WorkerRef}.
	 */
	@RequiredArgsConstructor
	class Forwardable {
		final Object request;
		final Exchange exchange;

		/**
		 * Forwards the {@code request} message to a {@link WorkerRef}.
		 * @param ref
		 */
		public void to( WorkerRef ref ) throws IOException {
			ref.send( exchange, request );
		}
	}
}
