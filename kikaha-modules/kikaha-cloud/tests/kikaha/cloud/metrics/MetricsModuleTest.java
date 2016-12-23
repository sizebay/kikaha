package kikaha.cloud.metrics;

import com.codahale.metrics.MetricRegistry;
import io.undertow.server.HttpHandler;
import kikaha.config.Config;
import kikaha.config.ConfigLoader;
import kikaha.config.MergeableConfig;
import kikaha.core.modules.http.WebResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static java.util.Arrays.asList;
import static kikaha.cloud.metrics.MetricsModule.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link MetricsModule}.
 */
@RunWith( MockitoJUnitRunner.class )
public class MetricsModuleTest {

    @Spy MetricRegistry metricRegistry;
    @Mock Config config;
    @Mock HttpHandler httpHandler;
    @Mock WebResource webResource;
    @Mock ReporterConfiguration reporterConfiguration;
    @Mock MetricRegistryConfiguration metricRegistryConfiguration;

    @Spy @InjectMocks
    MetricsModule module;

    @Before
    public void setUp() throws Exception {
        doReturn( "POST" ).when( webResource ).method();
        doReturn( "/path" ).when( webResource ).path();
        doReturn( reporterConfiguration ).when( module ).initializeReporter();

        final Config defaultConfiguration = ConfigLoader.loadDefaults().getConfig(JVM_METRICS);
        doReturn( defaultConfiguration ).when( config ).getConfig( eq( JVM_METRICS ) );

        module.mBeanServer = new Producers().produceMBeanServer();
        module.metricConfigurations = asList( metricRegistryConfiguration );
    }

    @Test
    public void shouldNotCustomizeWhenModuleIsDisabled(){
        doReturn( false ).when( config ).getBoolean( IS_MODULE_ENABLED );
        module.loadConfig();

        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        assertEquals( httpHandler, customizedHandler );
    }

    @Test
    public void shouldBeAbleToWrapAndStoreIndividualMetrics(){
        doReturn( true ).when( config ).getBoolean( IS_MODULE_ENABLED );
        doReturn( true ).when( config ).getBoolean( SHOULD_STORE_INDIVIDUAL_WEB_METRICS );
        module.loadConfig();

        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        assertEquals( customizedHandler.getClass(), MetricHttpHandler.class );
        final MetricHttpHandler metricHttpHandler = (MetricHttpHandler)customizedHandler;
        verify( metricRegistry ).timer( eq("kikaha.transactions.POST /path") );
        assertEquals( httpHandler, metricHttpHandler.httpHandler );
    }

    @Test
    public void shouldBeAbleToWrapAndStoreSummarizerMetrics(){
        doReturn( true ).when( config ).getBoolean( IS_MODULE_ENABLED );
        doReturn( true ).when( config ).getBoolean( SHOULD_STORE_SUMMARIZED_WEB_METRICS );
        module.loadConfig();

        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        assertEquals( customizedHandler.getClass(), MetricHttpHandler.class );
        final MetricHttpHandler metricHttpHandler = (MetricHttpHandler)customizedHandler;
        verify( metricRegistry ).timer( eq("kikaha.transactions.summarized") );
        assertEquals( httpHandler, metricHttpHandler.httpHandler );
    }

    @Test
    public void shouldBeAbleToWrapAndStoreSummarizedAndIndividualMetrics(){
        doReturn( true ).when( config ).getBoolean( IS_MODULE_ENABLED );
        doReturn( true ).when( config ).getBoolean( SHOULD_STORE_SUMMARIZED_WEB_METRICS );
        doReturn( true ).when( config ).getBoolean( SHOULD_STORE_INDIVIDUAL_WEB_METRICS );
        module.loadConfig();

        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        assertEquals( customizedHandler.getClass(), MetricHttpHandler.class );
        final MetricHttpHandler metricHttpHandler = (MetricHttpHandler)customizedHandler;
        final MetricHttpHandler wrappedMetricHttpHandler = (MetricHttpHandler)metricHttpHandler.httpHandler;
        assertEquals( httpHandler, wrappedMetricHttpHandler.httpHandler );

        verify( metricRegistry ).timer( eq("kikaha.transactions.summarized") );
        verify( metricRegistry ).timer( eq("kikaha.transactions.POST /path") );
    }

    @Test
    public void shouldBeAbleToStartAllJVMMetrics() throws IOException {
        doReturn( true ).when( config ).getBoolean( IS_MODULE_ENABLED );
        module.loadConfig();

        module.load( null, null );
        verify( metricRegistry, times( 41 ) ).register( startsWith( NAMESPACE_JVM ), any() );
        verify( metricRegistry, times( 20 ) ).register( startsWith( NAMESPACE_JVM + ".memory" ), any() );
        verify( metricRegistry, times( 6 ) ).register( startsWith( NAMESPACE_JVM + ".buffer-pool" ), any() );
        verify( metricRegistry, times( 10 ) ).register( startsWith( NAMESPACE_JVM + ".threads" ), any() );
        verify( metricRegistry, times( 4 ) ).register( startsWith( NAMESPACE_JVM + ".gc" ), any() );
        verify( metricRegistry, times( 1 ) ).register( startsWith( NAMESPACE_JVM + ".fd" ), any() );
    }

    @Test
    public void shouldBeAbleToStartTheReporterConfiguration() throws IOException {
        doReturn( MergeableConfig.create() ).when( config ).getConfig( eq( JVM_METRICS ) );
        doReturn(true).when(config).getBoolean(IS_MODULE_ENABLED);
        module.loadConfig();
        module.load( null, null );
        verify( reporterConfiguration ).configureAndStartReportFor( eq( metricRegistry ) );
    }

    @Test
    public void shouldBeAbleToCallAllMetricRegistryConfigurations() throws IOException {
        doReturn(true).when(config).getBoolean(IS_MODULE_ENABLED);
        module.loadConfig();
        module.load( null, null );
        verify( metricRegistryConfiguration ).configure( eq( metricRegistry ) );
    }
}