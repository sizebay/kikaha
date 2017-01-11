package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link RESTFulEndpointInboxSupplier}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RESTFulEndpointInboxSupplierTest {

    final static int HAS_NOT_REACH_LIMIT = 1, HAS_REACH_LIMIT = 0;

    @Mock Exchange exchange;
    @Mock EndpointInboxSupplier wrappedSupplier;

    @Test
    public void ensureCanRetrieveMessageFromWrappedSupplierIfNoMessageWasManuallyPolled() throws InterruptedException, EndpointInboxConsumerTimeoutException, IOException {
        doReturn( exchange ).when( wrappedSupplier ).receiveMessage();

        val restFulEndpointInboxSupplier = RESTFulEndpointInboxSupplier.wrap( wrappedSupplier, HAS_NOT_REACH_LIMIT );
        final Exchange receivedMessage = restFulEndpointInboxSupplier.receiveMessage();

        assertNotNull( receivedMessage );
        verify( wrappedSupplier ).receiveMessage();
        assertEquals( exchange, receivedMessage );
    }

    @Test
    public void ensureCanRetrieveMessageManuallyPolled() throws InterruptedException, EndpointInboxConsumerTimeoutException, IOException {
        doReturn( exchange ).when( wrappedSupplier ).receiveMessage();

        val restFulEndpointInboxSupplier = RESTFulEndpointInboxSupplier.wrap( wrappedSupplier, HAS_NOT_REACH_LIMIT );
        restFulEndpointInboxSupplier.poll( exchange );
        final Exchange receivedMessage = restFulEndpointInboxSupplier.receiveMessage();

        assertNotNull( receivedMessage );
        verify( wrappedSupplier, never() ).receiveMessage();
        assertEquals( exchange, receivedMessage );
    }

    @Test
    public void ensureCanPollMessageWhenTheCurrentQueueHaveNotReachItsLimit(){
        val restFulEndpointInboxSupplier = RESTFulEndpointInboxSupplier.wrap( wrappedSupplier, HAS_NOT_REACH_LIMIT );
        assertTrue( restFulEndpointInboxSupplier.poll( exchange ) );
    }

    @Test
    public void ensureCanNotPollMessageWhenTheCurrentQueueHaveReachItsLimit(){
        val restFulEndpointInboxSupplier = RESTFulEndpointInboxSupplier.wrap( wrappedSupplier, HAS_REACH_LIMIT );
        assertTrue( restFulEndpointInboxSupplier.poll( exchange ) );
    }
}