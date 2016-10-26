package kikaha.uworkers.api;

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
	 * Send a response to the sender.
	 *
	 * @param response
	 * @return
	 */
	<RESP> Exchange reply(RESP response);

	/**
	 * Send an error response to the sender.
	 *
	 * @param error
	 * @return
	 */
	Exchange reply(Throwable error);

	/**
	 * Forward a message to a given {@link WorkerRef}. Developers are encouraged to use
	 * this method instead of {@link WorkerRef#forward} in order to keep the code cleaner.
	 *
	 * @param request
	 * @param <NEW_REQ>
	 * @return
	 */
	default <NEW_REQ> Forwardable forward( NEW_REQ request ){
		return new Forwardable( request, this );
	}

	/**
	 * A helper class that holds an {@code exchange} to forward a {@code request} message to a {@link WorkerRef}.
	 */
	@RequiredArgsConstructor
	class Forwardable {
		final Object request;
		final Exchange exchange;

		/**
		 * Forwards the {@code request} message to a {@link WorkerRef}.
		 * @param ref
		 */
		public void to( WorkerRef ref ) {
			ref.forward( exchange, request );
		}
	}
}
