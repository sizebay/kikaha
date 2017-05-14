package kikaha.cloud.metrics;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import com.codahale.metrics.*;
import kikaha.core.cdi.CDI;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultMetricConfigurationTest {

	@Mock CDI cdi;
	@Mock MetricRegistry registry;
	@Mock MetricRegistryListener listener;
	@Mock MetricFilter filter;

	@Spy @InjectMocks
	DefaultRegistryConfiguration defaultRegistryConfiguration;

	@Before
	public void injectConfiguration(){
		defaultRegistryConfiguration.configuration = new MetricConfiguration(
			DefaultRegistryConfiguration.class,
			MetricRegistryListener.class, MetricFilter.class,
			true, true,
			true, cdi
		);

		doReturn( listener ).when( cdi ).load( eq(MetricRegistryListener.class) );
		doReturn( filter ).when( cdi ).load( eq(MetricFilter.class) );
	}

	@Test
	public void ensureCanCreateAListenerThatWrapsOriginalListenerAndFilter(){
		final MetricRegistryListener listener = defaultRegistryConfiguration.wrapListenerAndFilter();
		assertEquals( WrapperMetricRegistryListener.class, listener.getClass() );

		final WrapperMetricRegistryListener wrapper = (WrapperMetricRegistryListener)listener;
		assertEquals( this.listener, wrapper.listener );
		assertEquals( this.filter, wrapper.filter );
	}

	@Test
	public void ensureCanRegisterWrapperListener(){
		doReturn( listener ).when(defaultRegistryConfiguration).wrapListenerAndFilter();
		defaultRegistryConfiguration.configureAndStartReportFor( registry );
		verify( registry ).addListener( eq( listener ) );
	}
}