package kikaha.cloud.metrics;

import static kikaha.cloud.metrics.MetricsModule.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import java.io.IOException;
import java.util.Arrays;
import com.codahale.metrics.*;
import io.undertow.server.HttpHandler;
import kikaha.config.*;
import kikaha.core.cdi.CDI;
import kikaha.core.modules.http.WebResource;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link MetricsModule}.
 */
@RunWith( MockitoJUnitRunner.class )
public class MetricsModuleTest {

    @Mock CDI cdi;
    @Spy MetricRegistry metricRegistry;
    @Mock Config config;
    @Mock HttpHandler httpHandler;
    @Mock WebResource webResource;
    @Mock ReporterConfiguration reporterConfiguration;
    @Mock MetricRegistryConfiguration metricRegistryConfiguration;

    @Spy @InjectMocks
    MetricsModule module;

    @Before
    public void injectConfiguration(){
        module.configuration = new MetricConfiguration(
            ReporterConfiguration.class,
            MetricRegistryListener.class, MetricFilter.class,
            true, true,
            true, cdi
        );

        doReturn( reporterConfiguration ).when( cdi ).load( eq(ReporterConfiguration.class) );
    }

    @Before
    public void setUp() throws Exception {
        doReturn( "POST" ).when( webResource ).method();
        doReturn( "/path" ).when( webResource ).path();

        final Config defaultConfiguration = ConfigLoader.loadDefaults().getConfig(JVM_METRICS);
        doReturn( defaultConfiguration ).when( config ).getConfig( Matchers.eq( JVM_METRICS ) );

        module.mBeanServer = new Producers().produceMBeanServer();
        module.metricConfigurations = Arrays.asList( metricRegistryConfiguration );
    }

    @Test
    @Ignore
    public void shouldNotCustomizeWhenModuleIsDisabled(){
        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        Assert.assertEquals( httpHandler, customizedHandler );
    }

    @Test
    public void shouldBeAbleToWrapAndStoreIndividualMetrics(){
        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        Assert.assertEquals( customizedHandler.getClass(), MetricHttpHandler.class );
        verify( metricRegistry ).timer( Matchers.eq("kikaha.transactions.POST /path") );
    }

    @Test
    public void shouldBeAbleToWrapAndStoreSummarizerMetrics(){
        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        Assert.assertEquals( customizedHandler.getClass(), MetricHttpHandler.class );
        verify( metricRegistry ).timer( Matchers.eq("kikaha.transactions.summarized") );
    }

    @Test
    public void shouldBeAbleToWrapAndStoreSummarizedAndIndividualMetrics(){
        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        Assert.assertEquals( customizedHandler.getClass(), MetricHttpHandler.class );
        final MetricHttpHandler metricHttpHandler = (MetricHttpHandler)customizedHandler;
        final MetricHttpHandler wrappedMetricHttpHandler = (MetricHttpHandler)metricHttpHandler.httpHandler;
        assertEquals( httpHandler, wrappedMetricHttpHandler.httpHandler );

        verify( metricRegistry ).timer( Matchers.eq("kikaha.transactions.summarized") );
        verify( metricRegistry ).timer( Matchers.eq("kikaha.transactions.POST /path") );
    }

    @Ignore
    @Test
    public void shouldBeAbleToStartAllJVMMetrics() throws IOException {
        module.load( null, null );
        verify( metricRegistry, Mockito.times( 41 ) ).register( Matchers.startsWith( NAMESPACE_JVM ), Matchers.any() );
        verify( metricRegistry, Mockito.times( 20 ) ).register( Matchers.startsWith( NAMESPACE_JVM + ".memory" ), Matchers.any() );
        verify( metricRegistry, Mockito.times( 6 ) ).register( Matchers.startsWith( NAMESPACE_JVM + ".buffer-pool" ), Matchers.any() );
        verify( metricRegistry, Mockito.times( 10 ) ).register( Matchers.startsWith( NAMESPACE_JVM + ".threads" ), Matchers.any() );
        verify( metricRegistry, Mockito.times( 4 ) ).register( Matchers.startsWith( NAMESPACE_JVM + ".gc" ), Matchers.any() );
        verify( metricRegistry, Mockito.times( 1 ) ).register( Matchers.startsWith( NAMESPACE_JVM + ".fd" ), Matchers.any() );
    }

    @Test
    public void shouldBeAbleToStartTheReporterConfiguration() throws IOException {
        doReturn( MergeableConfig.create() ).when( config ).getConfig( Matchers.eq( JVM_METRICS ) );
        module.registerAvailableJvmMetrics();
        module.load( null, null );
        verify( reporterConfiguration ).configureAndStartReportFor( Matchers.eq( metricRegistry ) );
    }

    @Test
    public void shouldBeAbleToCallAllMetricRegistryConfigurations() throws IOException {
        module.registerAvailableJvmMetrics();
        module.load( null, null );
        verify( metricRegistryConfiguration ).configure( Matchers.eq( metricRegistry ) );
    }
}
