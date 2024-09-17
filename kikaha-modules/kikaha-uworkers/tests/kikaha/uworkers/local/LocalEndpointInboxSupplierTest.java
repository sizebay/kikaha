package kikaha.uworkers.local;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import java.io.IOException;
import java.util.concurrent.*;
import kikaha.uworkers.api.Exchange;
import org.junit.Test;

/**
 *
 */
public class LocalEndpointInboxSupplierTest {

	@Test
	public void ensureCanBeCreatedWithBoundedMessageQueue() {
		final LocalEndpointInboxSupplier supplier = LocalEndpointInboxSupplier.withFixedSize(1);
		assertTrue( ArrayBlockingQueue.class.isInstance( supplier.messageQueue ) );
		assertEquals( 1, supplier.messageQueue.remainingCapacity() );
	}

	@Test
	public void ensureCanBeCreatedWithUnboundedMessageQueue() {
		final LocalEndpointInboxSupplier supplier = LocalEndpointInboxSupplier.withElasticSize();
		assertTrue( LinkedBlockingQueue.class.isInstance( supplier.messageQueue ) );
		assertEquals( Integer.MAX_VALUE, supplier.messageQueue.remainingCapacity() );
	}

	@Test
	public void ensureCanInsertAndRetrieveObject() throws IOException, InterruptedException {
		final Exchange exchange = mock( Exchange.class );
		final LocalEndpointInboxSupplier supplier = LocalEndpointInboxSupplier.withFixedSize(1);
		supplier.sendMessage( exchange );
		assertEquals( exchange, supplier.receiveMessage() );
	}
}
