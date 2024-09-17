package kikaha.core.modules;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import kikaha.core.test.Exposed;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link GracefulShutdownHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class GracefulShutdownHandlerTest {

    HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

    @Mock HttpHandler httpHandler;
    GracefulShutdownHandler shutdownHandler;

    @Before
    public void setup(){
        shutdownHandler = new GracefulShutdownHandler(  httpHandler );
    }

    @Test
    public void ensureCanDelegateReceivedRequestsToAHttpHandler() throws Exception {
        shutdownHandler.handleRequest( exchange );
        verify( httpHandler ).handleRequest( eq( exchange ) );
    }

    @Test
    public void ensureDenyRequestsAfterAShutdown() throws Exception {
        shutdownHandler.shutdown();
        shutdownHandler.handleRequest( exchange );
        verify( httpHandler, never() ).handleRequest( eq( exchange ) );
        assertEquals(StatusCodes.SERVICE_UNAVAILABLE, exchange.getStatusCode());
    }

    @Test
    public void ensureIncreaseRequestCountWhenReceiveARequest() throws Exception {
        shutdownHandler.handleRequest( exchange );
        assertEquals( 1, shutdownHandler.getActiveRequests() );
    }

    @Test
    public void ensureDecreaseTheRequestCountWhenTheExchangeIsFinished() throws Exception {
        final Exposed exposedExchange = new Exposed( exchange );
        shutdownHandler.handleRequest( exchange );
        exposedExchange.runMethodSilently( "invokeExchangeCompleteListeners" );
        assertEquals( 0, shutdownHandler.getActiveRequests() );
    }
}
