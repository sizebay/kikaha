package kikaha.cloud.metrics;

import com.codahale.metrics.MetricRegistry;
import io.undertow.server.HttpHandler;
import kikaha.config.Config;
import kikaha.core.modules.http.WebResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static kikaha.cloud.metrics.MetricsModule.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
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

    @Spy @InjectMocks
    MetricsModule module;

    @Before
    public void setUp() throws Exception {
        doReturn( "POST" ).when( webResource ).method();
        doReturn( "/path" ).when( webResource ).path();
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
}