package kikaha.uworkers.local;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import java.util.concurrent.BlockingQueue;
import kikaha.uworkers.api.*;
import lombok.SneakyThrows;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalWorkerRefTest {

	@Mock BlockingQueue queue;
	LocalEndpointInboxSupplier supplier;
	WorkerRef workerRef;

	@Before
	public void configureTest(){
		supplier = spy( new LocalEndpointInboxSupplier( queue ) );
		workerRef = new LocalWorkerRef(supplier);
	}

	@Test
	@SneakyThrows
	public void ensureCanSendRawObject(){
		workerRef.send(new Object());
		verify( supplier ).sendMessage( any(Exchange.class) );
		verify( queue ).put( any(Exchange.class) );
	}

	@Test
	@SneakyThrows
	public void ensureCanSendExchange(){
		workerRef.send( LocalExchange.of( new Object() ) );
		verify( queue ).put( any(Exchange.class) );
	}
}
