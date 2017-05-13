package kikaha.cloud.metrics;

import com.codahale.metrics.Timer;
import io.undertow.server.*;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link MetricHttpHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MetricHttpHandlerTest {

    final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

    @Mock Timer timer;
    @Mock Timer.Context context;
    @Mock HttpHandler httpHandler;

    @Before
    public void setupMocks(){
        Mockito.doReturn( context ).when( timer ).time();
    }

    @Test
    public void shouldAlwaysDelegateTheRequestToTheTargetHttpHandler() throws Exception {
        final MetricHttpHandler metricHttpHandler = new MetricHttpHandler(httpHandler, timer);
        metricHttpHandler.handleRequest( exchange );
        Mockito.verify( httpHandler ).handleRequest( Matchers.eq( exchange ) );
    }

    @Test
    public void shouldAlwaysTrackRequestElapsedTime() throws Exception {
        final MetricHttpHandler metricHttpHandler = new MetricHttpHandler(httpHandler, timer);
        metricHttpHandler.handleRequest( exchange );
        Mockito.verify( timer, Mockito.times(1) ).time();
        Mockito.verify( context, Mockito.times(1) ).stop();
    }
}