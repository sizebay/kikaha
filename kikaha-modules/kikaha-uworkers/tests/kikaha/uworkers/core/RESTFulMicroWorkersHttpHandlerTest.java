package kikaha.uworkers.core;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import java.util.concurrent.atomic.AtomicReference;
import io.undertow.server.HttpServerExchange;
import kikaha.core.test.HttpServerExchangeStub;
import kikaha.urouting.*;
import kikaha.urouting.SimpleExchange.ContentReceiver;
import kikaha.uworkers.api.WorkerRef;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * Unit tests for {@link RESTFulMicroWorkersHttpHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RESTFulMicroWorkersHttpHandlerTest {

    final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
    final byte[] helloWorld = "Hello World".getBytes();

    @Mock WorkerRef workerRef;
    @Mock Object response;
    @Mock UndertowHelper helper;
    @Mock SimpleExchange httpExchange;
    @Mock EndpointInboxSupplier wrappedSupplier;
    RESTFulMicroWorkersHttpHandler handler;

    @Before
    public void setup(){
        doReturn( httpExchange ).when( helper ).simplify( any() );
        doAnswer( this::simulateReceiveBytes ).when( httpExchange ).receiveRequest( any() );
        handler = RESTFulMicroWorkersHttpHandler.with( helper, workerRef );
    }

    @Test
    public void ensureSendHttpExchangeWasPolledWhenAMessageWasReceived() throws Exception {
        handler.handleRequest( exchange );
        verify( workerRef ).send( any( RESTFulExchange.class ) );
    }

    @Test
    public void ensureSendAResponseWhenTheMicroWorkerExchangeReceiveAReply() throws Exception {
        final CaptureArg<RESTFulExchange> arg = CaptureArg.capture(0, RESTFulExchange.class);
        doAnswer( arg ).when( workerRef ).send( any(RESTFulExchange.class) );

        handler.handleRequest( exchange );
        final RESTFulExchange restFulExchange = arg.get();
        restFulExchange.reply( response );

        verify( httpExchange ).sendResponse( eq( response ) );
    }

    @SuppressWarnings( "unchecked" )
    Void simulateReceiveBytes( InvocationOnMock invocationOnMock ){
        try {
            final ContentReceiver<SimpleExchange,byte[]> receiver =
                invocationOnMock.getArgumentAt(0 , ContentReceiver.class );
            receiver.accept( httpExchange, helloWorld );
        } catch ( Throwable t ) { t.printStackTrace(); }
        return null;
    }
}

@RequiredArgsConstructor(staticName = "capture")
class CaptureArg<T> implements Answer<Void> {

    @Delegate
    final AtomicReference<T> holder = new AtomicReference<>();
    final int index;
    final Class<T> clazz;

    @Override
    public Void answer(InvocationOnMock invocation) throws Throwable {
        final T argumentAt = invocation.getArgumentAt(index, clazz);
        holder.set( argumentAt );
        return null;
    }
}