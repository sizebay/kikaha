package kikaha.cloud.metrics;

import com.codahale.metrics.Timer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        doReturn( context ).when( timer ).time();
    }

    @Test
    public void shouldAlwaysDelegateTheRequestToTheTargetHttpHandler() throws Exception {
        final MetricHttpHandler metricHttpHandler = new MetricHttpHandler(httpHandler, timer);
        metricHttpHandler.handleRequest( exchange );
        verify( httpHandler ).handleRequest( eq( exchange ) );
    }

    @Test
    public void shouldAlwaysTrackRequestElapsedTime() throws Exception {
        final MetricHttpHandler metricHttpHandler = new MetricHttpHandler(httpHandler, timer);
        metricHttpHandler.handleRequest( exchange );
        verify( timer, times(1) ).time();
        verify( context, times(1) ).stop();
    }
}