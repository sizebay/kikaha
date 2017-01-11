package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static java.lang.Thread.sleep;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EndpointInboxConsumer}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class EndpointInboxConsumerTest {

	Threads threads;

	@Mock Exchange exchange;
	@Mock EndpointInboxSupplier supplier;
	@Mock WorkerEndpointMessageListener listener;

	EndpointInboxConsumer consumer;

	@Before
	public void createConsumer(){
		threads = Threads.fixedPool(1);
		consumer = new EndpointInboxConsumer( supplier, listener, "default" );
	}

	@After
	public void shutdownThreads(){
		threads.shutdown();
	}

	@Test
	@SneakyThrows
	public void ensureCanListenerCanReceiveAMessageFromInbox(){
		doReturn( exchange ).when( supplier ).receiveMessage();

		threads.submit( consumer );
		sleep( 200 );

		verify( listener, atLeastOnce() ).onMessage( eq( exchange ) );
	}

	@Test
	@SneakyThrows
	public void ensureCanHandleFailuresRaisedFromWorkerTaskExecution(){
		final IOException exception = new IOException();
		doThrow( exception ).when( listener ).onMessage( eq( exchange ) );
		doReturn( exchange ).when( supplier ).receiveMessage();

		threads.submit( consumer );
		sleep( 200 );

		verify( exchange, atLeastOnce() ).reply( eq( exception ) );
	}

	@Test
	@SneakyThrows
	public void ensureFailuresWhenReceivesTheMessageWillStopTheListener(){
		final IOException exception = new IOException();
		doThrow( exception ).when( supplier ).receiveMessage();

		threads.submit( consumer );
		sleep( 200 );

		verify( exchange, never() ).reply( eq( exception ) );
	}
}
