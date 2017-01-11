package kikaha.uworkers.core;

import kikaha.urouting.SimpleExchange;
import kikaha.urouting.UndertowHelper;
import kikaha.uworkers.api.Exchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link RESTFulMicroWorkersHttpHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RESTFulMicroWorkersHttpHandlerTest {

    @Mock Object response;
    @Mock UndertowHelper helper;
    @Mock SimpleExchange httpExchange;
    @Mock EndpointInboxSupplier wrappedSupplier;
    RESTFulEndpointInboxSupplier supplier;
    RESTFulMicroWorkersHttpHandler handler;

    @Before
    public void setup(){
        doReturn( httpExchange ).when( helper ).simplify( any() );
        supplier = RESTFulEndpointInboxSupplier.wrap( wrappedSupplier, 1 );
        handler = RESTFulMicroWorkersHttpHandler.with( helper, supplier );
    }

    @Test
    public void ensureSendHttpExchangeWasPolledWhenAMessageWasReceived() throws Exception {
        handler.handleRequest( null );
        final Exchange polledExchange = supplier.receiveMessage();
        assertTrue( polledExchange instanceof RESTFulExchange );
    }

    @Test
    public void ensureSendAResponseWhenTheMicroWorkerExchangeReceiveAReply() throws Exception {
        handler.handleRequest( null );
        supplier.receiveMessage().reply( response );
        verify( httpExchange ).sendResponse( eq( response ) );
    }

    @Test
    public void ensureSendAFailureResponseWhenTheMicroWorkerInboxIsFull() throws Exception {
        handler.handleRequest( null );
        handler.handleRequest( null );
        verify( httpExchange ).sendResponse( eq( RESTFulMicroWorkersHttpHandler.TOO_MANY_REQUESTS ) );

        supplier.receiveMessage().reply( response );
        verify( httpExchange ).sendResponse( eq( response ) );
    }
}