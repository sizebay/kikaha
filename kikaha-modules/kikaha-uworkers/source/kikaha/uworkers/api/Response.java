package kikaha.uworkers.api;

import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;

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
	Response then( BiConsumer<UndefinedObject, Throwable> listener );

	/**
	 * Store an object which its class is not initially known. It allow developers
	 * to as the held object to a more convenient type more easily.
	 */
	@RequiredArgsConstructor
	class UndefinedObject {
		final Object object;

		public <T> T as(Class<T> type ) {
			return (T) object;
		}
	}
}
