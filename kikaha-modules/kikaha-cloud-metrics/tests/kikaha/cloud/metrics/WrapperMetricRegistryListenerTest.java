package kikaha.cloud.metrics;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import com.codahale.metrics.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class WrapperMetricRegistryListenerTest {

	@Mock MetricRegistryListener listener;
	@Mock MetricFilter filter;

	WrapperMetricRegistryListener wrapper;

	@Before
	public void instantiateWrapper(){
		wrapper = new WrapperMetricRegistryListener( filter, listener );
	}

	@Test
	public void ensureCanAddGaugeWhenAllowedByTheFilter() throws Exception {
		Gauge<Object> metric = mock( Gauge.class );
		doReturn( true ).when( filter ).matches( eq("name"), eq( metric ) );
		wrapper.onGaugeAdded( "name", metric );
		verify( listener ).onGaugeAdded( eq("name"), eq( metric ) );
	}

	@Test
	public void ensureCanNotAddGaugeWhenNotAllowedByTheFilter() throws Exception {
		Gauge<Object> metric = mock( Gauge.class );
		doReturn( false ).when( filter ).matches( eq("name"), eq( metric ) );
		wrapper.onGaugeAdded( "name", metric );
		verify( listener, never() ).onGaugeAdded( eq("name"), eq( metric ) );
	}

	@Test
	public void ensureCanAddCounterWhenAllowedByTheFilter() throws Exception {
		Counter metric = mock( Counter.class );
		doReturn( true ).when( filter ).matches( eq("name"), eq( metric ) );
		wrapper.onCounterAdded( "name", metric );
		verify( listener ).onCounterAdded( eq("name"), eq( metric ) );
	}

	@Test
	public void ensureCanNotAddCounterWhenNotAllowedByTheFilter() throws Exception {
		Counter metric = mock( Counter.class );
		doReturn( false ).when( filter ).matches( eq("name"), eq( metric ) );
		wrapper.onCounterAdded( "name", metric );
		verify( listener, never() ).onCounterAdded( eq("name"), eq( metric ) );
	}

	@Test
	public void ensureCanAddHistogramWhenAllowedByTheFilter() throws Exception {
		Histogram metric = mock( Histogram.class );
		doReturn( true ).when( filter ).matches( eq("name"), eq( metric ) );
		wrapper.onHistogramAdded( "name", metric );
		verify( listener ).onHistogramAdded( eq("name"), eq( metric ) );
	}

	@Test
	public void ensureCanNotAddHistogramWhenNotAllowedByTheFilter() throws Exception {
		Histogram metric = mock( Histogram.class );
		doReturn( false ).when( filter ).matches( eq("name"), eq( metric ) );
		wrapper.onHistogramAdded( "name", metric );
		verify( listener, never() ).onHistogramAdded( eq("name"), eq( metric ) );
	}

	@Test
	public void ensureCanAddMeterWhenAllowedByTheFilter() throws Exception {
		Meter metric = mock( Meter.class );
		doReturn( true ).when( filter ).matches( eq("name"), eq( metric ) );
		wrapper.onMeterAdded( "name", metric );
		verify( listener ).onMeterAdded( eq("name"), eq( metric ) );
	}

	@Test
	public void ensureCanNotAddMeterWhenNotAllowedByTheFilter() throws Exception {
		Meter metric = mock( Meter.class );
		doReturn( false ).when( filter ).matches( eq("name"), eq( metric ) );
		wrapper.onMeterAdded( "name", metric );
		verify( listener, never() ).onMeterAdded( eq("name"), eq( metric ) );
	}

	@Test
	public void ensureCanAddTimerWhenAllowedByTheFilter() throws Exception {
		Timer metric = mock( Timer.class );
		doReturn( true ).when( filter ).matches( eq("name"), eq( metric ) );
		wrapper.onTimerAdded( "name", metric );
		verify( listener ).onTimerAdded( eq("name"), eq( metric ) );
	}

	@Test
	public void ensureCanNotAddTimerWhenNotAllowedByTheFilter() throws Exception {
		Timer metric = mock( Timer.class );
		doReturn( false ).when( filter ).matches( eq("name"), eq( metric ) );
		wrapper.onTimerAdded( "name", metric );
		verify( listener, never() ).onTimerAdded( eq("name"), eq( metric ) );
	}

	@Test( expected = UnsupportedOperationException.class )
	public void onGaugeRemoved() throws Exception {
		wrapper.onGaugeRemoved( "any" );
	}

	@Test( expected = UnsupportedOperationException.class )
	public void onCounterRemoved() throws Exception {
		wrapper.onCounterRemoved( "any" );
	}

	@Test( expected = UnsupportedOperationException.class )
	public void onHistogramRemoved() throws Exception {
		wrapper.onHistogramRemoved( "any" );
	}

	@Test( expected = UnsupportedOperationException.class )
	public void onMeterRemoved() throws Exception {
		wrapper.onMeterRemoved( "any" );
	}

	@Test( expected = UnsupportedOperationException.class )
	public void onTimerRemoved() throws Exception {
		wrapper.onTimerRemoved( "any" );
	}

}