package kikaha.cloud.metrics;

import static kikaha.cloud.metrics.MetricsModule.*;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.Arrays;
import com.codahale.metrics.MetricRegistry;
import io.undertow.server.HttpHandler;
import kikaha.config.*;
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
        Mockito.doReturn( "POST" ).when( webResource ).method();
        Mockito.doReturn( "/path" ).when( webResource ).path();
        Mockito.doReturn( reporterConfiguration ).when( module ).initializeReporter();

        final Config defaultConfiguration = ConfigLoader.loadDefaults().getConfig(JVM_METRICS);
        Mockito.doReturn( defaultConfiguration ).when( config ).getConfig( Matchers.eq( JVM_METRICS ) );

        module.mBeanServer = new Producers().produceMBeanServer();
        module.metricConfigurations = Arrays.asList( metricRegistryConfiguration );
    }

    @Test
    public void shouldNotCustomizeWhenModuleIsDisabled(){
        Mockito.doReturn( false ).when( config ).getBoolean( IS_MODULE_ENABLED );
        module.loadConfig();

        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        Assert.assertEquals( httpHandler, customizedHandler );
    }

    @Test
    public void shouldBeAbleToWrapAndStoreIndividualMetrics(){
        Mockito.doReturn( true ).when( config ).getBoolean( IS_MODULE_ENABLED );
        Mockito.doReturn( true ).when( config ).getBoolean( SHOULD_STORE_INDIVIDUAL_WEB_METRICS );
        module.loadConfig();

        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        Assert.assertEquals( customizedHandler.getClass(), MetricHttpHandler.class );
        final MetricHttpHandler metricHttpHandler = (MetricHttpHandler)customizedHandler;
        Mockito.verify( metricRegistry ).timer( Matchers.eq("kikaha.transactions.POST /path") );
        assertEquals( httpHandler, metricHttpHandler.httpHandler );
    }

    @Test
    public void shouldBeAbleToWrapAndStoreSummarizerMetrics(){
        Mockito.doReturn( true ).when( config ).getBoolean( IS_MODULE_ENABLED );
        Mockito.doReturn( true ).when( config ).getBoolean( SHOULD_STORE_SUMMARIZED_WEB_METRICS );
        module.loadConfig();

        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        Assert.assertEquals( customizedHandler.getClass(), MetricHttpHandler.class );
        final MetricHttpHandler metricHttpHandler = (MetricHttpHandler)customizedHandler;
        Mockito.verify( metricRegistry ).timer( Matchers.eq("kikaha.transactions.summarized") );
        assertEquals( httpHandler, metricHttpHandler.httpHandler );
    }

    @Test
    public void shouldBeAbleToWrapAndStoreSummarizedAndIndividualMetrics(){
        Mockito.doReturn( true ).when( config ).getBoolean( IS_MODULE_ENABLED );
        Mockito.doReturn( true ).when( config ).getBoolean( SHOULD_STORE_SUMMARIZED_WEB_METRICS );
        Mockito.doReturn( true ).when( config ).getBoolean( SHOULD_STORE_INDIVIDUAL_WEB_METRICS );
        module.loadConfig();

        final HttpHandler customizedHandler = module.customize(httpHandler, webResource);
        Assert.assertEquals( customizedHandler.getClass(), MetricHttpHandler.class );
        final MetricHttpHandler metricHttpHandler = (MetricHttpHandler)customizedHandler;
        final MetricHttpHandler wrappedMetricHttpHandler = (MetricHttpHandler)metricHttpHandler.httpHandler;
        assertEquals( httpHandler, wrappedMetricHttpHandler.httpHandler );

        Mockito.verify( metricRegistry ).timer( Matchers.eq("kikaha.transactions.summarized") );
        Mockito.verify( metricRegistry ).timer( Matchers.eq("kikaha.transactions.POST /path") );
    }

    @Ignore
    @Test
    public void shouldBeAbleToStartAllJVMMetrics() throws IOException {
        Mockito.doReturn( true ).when( config ).getBoolean( IS_MODULE_ENABLED );
        module.loadConfig();

        module.load( null, null );
        Mockito.verify( metricRegistry, Mockito.times( 41 ) ).register( Matchers.startsWith( NAMESPACE_JVM ), Matchers.any() );
        Mockito.verify( metricRegistry, Mockito.times( 20 ) ).register( Matchers.startsWith( NAMESPACE_JVM + ".memory" ), Matchers.any() );
        Mockito.verify( metricRegistry, Mockito.times( 6 ) ).register( Matchers.startsWith( NAMESPACE_JVM + ".buffer-pool" ), Matchers.any() );
        Mockito.verify( metricRegistry, Mockito.times( 10 ) ).register( Matchers.startsWith( NAMESPACE_JVM + ".threads" ), Matchers.any() );
        Mockito.verify( metricRegistry, Mockito.times( 4 ) ).register( Matchers.startsWith( NAMESPACE_JVM + ".gc" ), Matchers.any() );
        Mockito.verify( metricRegistry, Mockito.times( 1 ) ).register( Matchers.startsWith( NAMESPACE_JVM + ".fd" ), Matchers.any() );
    }

    @Test
    public void shouldBeAbleToStartTheReporterConfiguration() throws IOException {
        Mockito.doReturn( MergeableConfig.create() ).when( config ).getConfig( Matchers.eq( JVM_METRICS ) );
        Mockito.doReturn(true).when(config).getBoolean(IS_MODULE_ENABLED);
        module.loadConfig();
        module.load( null, null );
        Mockito.verify( reporterConfiguration ).configureAndStartReportFor( Matchers.eq( metricRegistry ) );
    }

    @Test
    public void shouldBeAbleToCallAllMetricRegistryConfigurations() throws IOException {
        Mockito.doReturn(true).when(config).getBoolean(IS_MODULE_ENABLED);
        module.loadConfig();
        module.load( null, null );
        Mockito.verify( metricRegistryConfiguration ).configure( Matchers.eq( metricRegistry ) );
    }
}
