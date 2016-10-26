package kikaha.uworkers.api;

import java.util.function.BiConsumer;

/**
 * Represents a response promise from a given task sent to a Worker.
 */
public interface Response {

	/**
	 * Awaits for a response to come.
	 *
	 * @return
	 */
	<RESP> RESP response();

	/**
	 * Attach a listener that will be notified when the response is available.
	 *
	 * @param listener
	 * @return
	 */
	<RESP> Response then( BiConsumer<RESP, Throwable> listener );
}
