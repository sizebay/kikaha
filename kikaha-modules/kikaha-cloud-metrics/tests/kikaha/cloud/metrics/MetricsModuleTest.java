package kikaha.cloud.metrics;

import static kikaha.cloud.metrics.MetricsModule.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.Arrays;
import com.codahale.metrics.*;
import io.undertow.server.HttpHandler;
import kikaha.config.*;
import kikaha.core.DeploymentContext;
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
            MetricRegistryListener.class, MetricFilter.class, MetricStore.class,
            true, true,
            true, 1000, cdi
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
    public void shouldBeAbleToWrapAndStoreSummarizerMetrics() throws IOException {
        final DeploymentContext deploymentContext = new DeploymentContext();
        module.registerAvailableJvmMetrics();
        module.load(null, deploymentContext);
        Assert.assertEquals( deploymentContext.rootHandler().getClass(), MetricHttpHandler.class );
        verify( metricRegistry ).timer( Matchers.eq("kikaha.transactions.summarized") );
    }

    @Ignore
    @Test
    public void shouldBeAbleToStartAllJVMMetrics() throws IOException {
        final DeploymentContext deploymentContext = new DeploymentContext();
        module.registerAvailableJvmMetrics();
        module.load(null, deploymentContext);
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
        final DeploymentContext deploymentContext = new DeploymentContext();
        module.registerAvailableJvmMetrics();
        module.load(null, deploymentContext);
        verify( reporterConfiguration ).configureAndStartReportFor( Matchers.eq( metricRegistry ) );
    }

    @Test
    public void shouldBeAbleToCallAllMetricRegistryConfigurations() throws IOException {
        final DeploymentContext deploymentContext = new DeploymentContext();
        module.registerAvailableJvmMetrics();
        module.load(null, deploymentContext);
        verify( metricRegistryConfiguration ).configure( Matchers.eq( metricRegistry ) );
    }
}
