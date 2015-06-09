package kikaha.hazelcast;

public abstract class Wait {

	/**
	 * Non-blocking, but CPU throttle, wait routine.<br>
	 * <br>
	 * <b>Note:</b> Use this for test propose only.
	 * 
	 * @param secs
	 */
	public static void seconds( int secs ) {
		long start = System.currentTimeMillis();
		long elapsed;
		do {
			elapsed = System.currentTimeMillis() - start;
		} while ( elapsed < ( 1000 * secs ) );
	}
}
