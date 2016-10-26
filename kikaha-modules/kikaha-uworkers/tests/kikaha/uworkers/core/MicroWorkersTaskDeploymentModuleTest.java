package kikaha.uworkers.core;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import java.io.IOException;
import javax.inject.Inject;
import kikaha.core.test.KikahaRunner;
import kikaha.uworkers.api.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 * Unit test for {@code MicroWorkersTaskDeploymentModule}.
 */
@RunWith(KikahaRunner.class)
public class MicroWorkersTaskDeploymentModuleTest {

	final WorkerEndpointMessageListener first = new FirstWorkerEndpointMessageListener();
	final WorkerEndpointMessageListener second = new SecondWorkerEndpointMessageListener();
	final WorkerEndpointMessageListener configured = new ConfiguredWorkerEndpointMessageListener();
	final WorkerEndpointMessageListener notConfigured = new NotConfiguredWorkerEndpointMessageListener();

	@Inject MicroWorkersTaskDeploymentModule module;

	@Mock EndpointInboxSupplier inbox;
	@Mock EndpointInboxSupplierFactory inboxSupplierFactory;
	@Mock WorkerEndpointMessageListener listener;

	@Before
	public void injectMocks(){
		MockitoAnnotations.initMocks(this);
		module = spy( module );
		module.factories = asList( inboxSupplierFactory );
		doReturn( true ).when( inboxSupplierFactory ).canHandleEndpoint( anyString() );
		doReturn( inbox ).when( inboxSupplierFactory ).createSupplier( anyString() );
	}

	@Test
	public void ensureThatCanDeployAllConsumers() throws IOException {
		module.consumers = asList( first, second );

		module.load( null, null );
		verify( module, times(2) ).deploy( any(WorkerEndpointMessageListener.class), eq(1) );
	}

	@Test
	public void ensureThatCanDeployConfigureNamedConsumer() throws IOException {
		module.config = spy( module.config );
		module.consumers = asList( first, second );

		module.load( null, null );

		verify( module.config ).getInteger( eq( "server.uworkers.first.parallelism" ), eq(1) );
		verify( module.config ).getInteger( eq( "server.uworkers.second.parallelism" ), eq(1) );
	}

	@Test
	public void ensureThatUsesDefaultParallelismWhenNoDataIsAvailableForAGivenNamedConsumer() throws IOException {
		module.config = spy( module.config );
		module.consumers = asList( configured );

		module.load( null, null );

		verify( module ).runInBackgroundWithParallelism( any(EndpointInboxConsumer.class), eq(2) );
	}

	@Test
	public void ensureThatUsesItsOwnParallelismWhenItIsConfiguredOnTheConfigurationFile() throws IOException {
		module.config = spy( module.config );
		module.consumers = asList( notConfigured );

		module.load( null, null );
		verify( module ).runInBackgroundWithParallelism( any(EndpointInboxConsumer.class), eq(1) );
	}
}

@Worker( endpoint = "first", alias = "first" )
class FirstWorkerEndpointMessageListener implements WorkerEndpointMessageListener {

	@Override
	public void onMessage(Exchange exchange) throws Throwable {
		throw new UnsupportedOperationException("onMessage not implemented yet!");
	}
}

@Worker( endpoint = "second", alias = "second" )
class SecondWorkerEndpointMessageListener implements WorkerEndpointMessageListener {

	@Override
	public void onMessage(Exchange exchange) throws Throwable {
		throw new UnsupportedOperationException("onMessage not implemented yet!");
	}
}

@Worker( endpoint = "configured", alias = "configured" )
class ConfiguredWorkerEndpointMessageListener implements WorkerEndpointMessageListener {

	@Override
	public void onMessage(Exchange exchange) throws Throwable {
		throw new UnsupportedOperationException("onMessage not implemented yet!");
	}
}

@Worker( endpoint = "not-configured", alias = "not-configured" )
class NotConfiguredWorkerEndpointMessageListener implements WorkerEndpointMessageListener {

	@Override
	public void onMessage(Exchange exchange) throws Throwable {
		throw new UnsupportedOperationException("onMessage not implemented yet!");
	}
}