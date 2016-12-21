package kikaha.cloud.metrics;

/**
 * Represents a read only data.
 */
public interface ReadOnlyData<T extends Metric> {

	/**
	 * Retrieves an read-only data. It is expected that the returned object
	 * is immutable.
	 *
	 * @return
	 */
	T getData();
}
