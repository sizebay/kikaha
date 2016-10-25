package kikaha.uworkers.api;

/**
 * A reference to a worker endpoint.
 */
public interface WorkerRef {

	/**
	 * Send a new request to the Worker referenced by this {@link WorkerRef}.
	 *
	 * @param request
	 * @param <REQ>
	 * @param <RESP>
	 * @return
	 */
	<REQ, RESP> Response<REQ, RESP> send(REQ request);

	/**
	 * Forward a received message {@code request} to the Worker referenced by {@link WorkerRef}.
	 * Whoever receive this message will be able to send a reply to the original sender that the
	 * current {@link Exchange} is holding.
	 *
	 * @param exchange
	 * @param request
	 * @param <REQ>
	 * @param <RESP>
	 */
	<REQ, RESP> void forward( Exchange<?, RESP> exchange, REQ request );
}
