package kikaha.uworkers.core;

import kikaha.urouting.SimpleExchange;
import kikaha.uworkers.api.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link RESTFulUWorkerResponse}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RESTFulUWorkerResponseTest {

    @Mock SimpleExchange simpleExchange;
    @Mock Response.UndefinedObject undefinedResponse;
    @Mock Throwable throwable;
    RESTFulUWorkerResponse workerResponse;

    @Before
    public void setup(){
        workerResponse = RESTFulUWorkerResponse.to( simpleExchange );
    }

    @Test
    public void ensureThatIsAbleToSendResponseToHttpClient() throws IOException {
        final Object expectedObject = new Object();
        doReturn( expectedObject ).when( undefinedResponse ).get();

        workerResponse.accept( undefinedResponse, null );
        verify( simpleExchange ).sendResponse( eq( expectedObject ) );
    }

    @Test
    public void ensureThatIsAbleToSendFailureResponseToHttpClient() throws IOException {
        workerResponse.accept( null, throwable );
        verify( simpleExchange ).sendResponse( eq( throwable ) );
    }
}