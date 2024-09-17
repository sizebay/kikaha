package kikaha.cloud.metrics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import com.codahale.metrics.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A pre-defined reporter configuration. It will ensure that all common routines
 * required to sent metrics to some kind of Metric Store.
 */
@Slf4j
public class DefaultReporterConfiguration implements ReporterConfiguration {

	@Inject MetricConfiguration configuration;
	@Inject MetricStoreHealthCheck healthCheck;

	@Override
	public void configureAndStartReportFor(MetricRegistry registry) {
		final MetricRegistryListener defaultListener = wrapListenerAndFilter();
		registry.addListener( defaultListener );

		final DefaultScheduledReporter reporter = new DefaultScheduledReporter(
			registry, configuration.metricStore(), healthCheck,
			configuration.metricFilter(), MILLISECONDS, MILLISECONDS );

		reporter.start( configuration.reportInterval(), MILLISECONDS );
	}

	MetricRegistryListener wrapListenerAndFilter(){
		return new WrapperMetricRegistryListener(
			configuration.metricFilter(), configuration.registryListener() );
	}

	/**
	 * A simple {@link MetricFilter} that does not discard any metric it receives.
	 */
	public static class AllowEveryThing implements MetricFilter {

		@Override
		public boolean matches(String name, Metric metric) {
			return true;
		}
	}

	/**
	 * A dummy {@link MetricRegistryListener} implementation that does absolutely nothing.
	 */
	public static class DoNothing extends MetricRegistryListener.Base {

	}
}

/**
 * A basic wrapper for the {@link MetricRegistryListener}. Basically, it will
 * notify the listener only if allowed by the {@link MetricFilter}.
 */
@Slf4j
@RequiredArgsConstructor
class WrapperMetricRegistryListener implements MetricRegistryListener {

	final MetricFilter filter;
	final MetricRegistryListener listener;

	@Override
	public void onGaugeAdded(String name, Gauge<?> gauge) {
		try {
			if (filter.matches(name, gauge))
				listener.onGaugeAdded( name, gauge );
		} catch ( Throwable cause ) {
			log.error( "Could not register metric " + name, cause );
		}
	}

	@Override
	public void onCounterAdded(String name, Counter counter) {
		try {
			if (filter.matches(name, counter))
				listener.onCounterAdded( name, counter );
		} catch ( Throwable cause ) {
			log.error( "Could not register metric " + name, cause );
		}
	}

	@Override
	public void onHistogramAdded(String name, Histogram histogram) {

		try {
			if (filter.matches(name, histogram))
				listener.onHistogramAdded( name, histogram );
		} catch ( Throwable cause ) {
			log.error( "Could not register metric " + name, cause );
		}
	}

	@Override
	public void onMeterAdded(String name, Meter meter) {

		try {
			if (filter.matches(name, meter))
				listener.onMeterAdded( name, meter );
		} catch ( Throwable cause ) {
			log.error( "Could not register metric " + name, cause );
		}
	}

	@Override
	public void onTimerAdded(String name, Timer timer) {
		try {
			if (filter.matches(name, timer))
				listener.onTimerAdded( name, timer );
		} catch ( Throwable cause ) {
			log.error( "Could not register metric " + name, cause );
		}
	}

	@Override
	public void onGaugeRemoved(String name) {
		throw new UnsupportedOperationException("Can't remove metrics from registry. Please reload the application.");
	}

	@Override
	public void onCounterRemoved(String name) {
		throw new UnsupportedOperationException("Can't remove metrics from registry. Please reload the application.");
	}

	@Override
	public void onHistogramRemoved(String name) {
		throw new UnsupportedOperationException("Can't remove metrics from registry. Please reload the application.");
	}

	@Override
	public void onMeterRemoved(String name) {
		throw new UnsupportedOperationException("Can't remove metrics from registry. Please reload the application.");
	}

	@Override
	public void onTimerRemoved(String name) {
		throw new UnsupportedOperationException("Can't remove metrics from registry. Please reload the application.");
	}
}

/**
 * Default implementation of Codahale's ScheduledReporter. Basically, it
 * delegates the report process the the {@link MetricStore} implementation.
 * Also, it will ensure that the server is unhealthy if it fails to report the metric.
 */
@Slf4j
class DefaultScheduledReporter extends ScheduledReporter {

	final MetricStore store;
	final MetricStoreHealthCheck healthCheck;

	public DefaultScheduledReporter(MetricRegistry registry, MetricStore store, MetricStoreHealthCheck healthCheck, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit) {
		super(registry, store.getName(), filter, rateUnit, durationUnit);
		this.store = store;
		this.healthCheck = healthCheck;
	}

	@Override
	public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
		log.debug( "Sending metrics to " + store.getName() );
		try {
			store.reportCounters(counters);
			store.reportGauges(gauges);
			store.reportHistograms(histograms);
			store.reportTimers(timers);
			store.reportMeters(meters);
			healthCheck.setHealthy();
		} catch ( Throwable cause ) {
			healthCheck.setUnhealthy( cause );
		}
	}
}