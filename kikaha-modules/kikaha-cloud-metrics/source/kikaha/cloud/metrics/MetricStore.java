package kikaha.cloud.metrics;

import java.util.SortedMap;
import com.codahale.metrics.*;

/**
 * Report metrics to a metric store.
 */
public interface MetricStore {

	/**
	 * Report gauges to a metric store.
	 * @param gauges
	 */
	@SuppressWarnings("unchecked")
	void reportGauges( SortedMap<String,Gauge> gauges );

	/**
	 * Report counters to a metric store.
	 * @param counters
	 */
	void reportCounters( SortedMap<String,Counter> counters );

	/**
	 * Report histograms to a metric store.
	 * @param histogram
	 */
	void reportHistograms( SortedMap<String,Histogram> histogram );

	/**
	 * Report meters to a metric store.
	 * @param meters
	 */
	void reportMeters( SortedMap<String,Meter> meters );

	/**
	 * Report timers to a metric store.
	 * @param timers
	 */
	void reportTimers(SortedMap<String,Timer> timers );

	default String getName(){
		return getClass().getCanonicalName();
	}
}
