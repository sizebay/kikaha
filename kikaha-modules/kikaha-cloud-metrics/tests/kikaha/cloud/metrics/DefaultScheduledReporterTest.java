package kikaha.cloud.metrics;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultScheduledReporterTest {

	@Mock Gauge gauge;
	@Mock Counter counter;
	@Mock Histogram histogram;
	@Mock Meter meter;
	@Mock Timer timer;

	@Mock MetricStore store;

	@Test
	public void ensureDelegatesGaugesToStore(){
		final DefaultScheduledReporter defaultReporter = createDefaultReporter();

		final SortedMap<String, Gauge> metricMap = sortedMap("name", gauge);
		defaultReporter.report(
			metricMap,
			sortedMap( "name", counter ),
			sortedMap( "name", histogram ),
			sortedMap( "name", meter ),
			sortedMap( "name", timer )
		);

		verify( store ).reportGauges( eq( metricMap ) );
	}

	@Test
	public void ensureDelegatesCounterToStore(){
		final DefaultScheduledReporter defaultReporter = createDefaultReporter();

		final SortedMap<String, Counter> metricMap = sortedMap("name", counter );
		defaultReporter.report(
				sortedMap( "name", gauge ),
				metricMap,
				sortedMap( "name", histogram ),
				sortedMap( "name", meter ),
				sortedMap( "name", timer )
		);

		verify( store ).reportCounters( eq( metricMap ) );
	}

	@Test
	public void ensureDelegatesHistogramToStore(){
		final DefaultScheduledReporter defaultReporter = createDefaultReporter();

		final SortedMap<String, Histogram> metricMap = sortedMap("name", histogram );
		defaultReporter.report(
				sortedMap( "name", gauge ),
				sortedMap( "name", counter ),
				metricMap,
				sortedMap( "name", meter ),
				sortedMap( "name", timer )
		);

		verify( store ).reportHistograms( eq( metricMap ) );
	}

	@Test
	public void ensureDelegatesMeterToStore(){
		final DefaultScheduledReporter defaultReporter = createDefaultReporter();

		final SortedMap<String, Meter> metricMap = sortedMap("name", meter );
		defaultReporter.report(
				sortedMap( "name", gauge ),
				sortedMap( "name", counter ),
				sortedMap( "name", histogram ),
				metricMap,
				sortedMap( "name", timer )
		);

		verify( store ).reportMeters( eq( metricMap ) );
	}

	@Test
	public void ensureDelegatesTimerToStore(){
		final DefaultScheduledReporter defaultReporter = createDefaultReporter();

		final SortedMap<String, Timer> metricMap = sortedMap("name", timer );
		defaultReporter.report(
				sortedMap( "name", gauge ),
				sortedMap( "name", counter ),
				sortedMap( "name", histogram ),
				sortedMap( "name", meter ),
				metricMap
				);

		verify( store ).reportTimers( eq( metricMap ) );
	}

	DefaultScheduledReporter createDefaultReporter(){
		return new DefaultScheduledReporter(
			new MetricRegistry(), store, new MetricStoreHealthCheck(),
			new DefaultReporterConfiguration.AllowEveryThing(),
			TimeUnit.MILLISECONDS, TimeUnit.MICROSECONDS );
	}

	<T> SortedMap<String, T> sortedMap( String name, T metric ){
		SortedMap<String, T> map = new TreeMap<>();
		map.put( name, metric );
		return map;
	}
}