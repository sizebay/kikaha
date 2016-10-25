package kikaha.uworkers.api;

import java.util.function.BiConsumer;

/**
 * Represents a response promise from a given task sent to a Worker.
 */
public interface Response<REQ,RESP> {

	/**
	 * Awaits for a response to come.
	 *
	 * @return
	 */
	RESP response();

	/**
	 * Attach a listener that will be notified when the response is available.
	 *
	 * @param listener
	 * @return
	 */
	Response<REQ, RESP> then( BiConsumer<RESP, Throwable> listener );
}
