package kikaha.cloud.metrics;

import javax.inject.Inject;
import com.codahale.metrics.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class DefaultRegistryConfiguration implements ReporterConfiguration {

	@Inject MetricConfiguration configuration;

	@Override
	public void configureAndStartReportFor(MetricRegistry registry) {
		final MetricRegistryListener defaultListener = wrapListenerAndFilter();
		registry.addListener( defaultListener );
	}

	MetricRegistryListener wrapListenerAndFilter(){
		return new WrapperMetricRegistryListener(
			configuration.metricFilter(), configuration.registryListener() );
	}

	public static class AllowEveryThing implements MetricFilter {

		@Override
		public boolean matches(String name, Metric metric) {
			return true;
		}
	}
}

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