package kikaha.uworkers.core;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import java.io.IOException;
import javax.inject.Inject;
import kikaha.config.Config;
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
	@Inject EndpointContext endpointContext;
	@Inject Config config;

	@Mock EndpointInboxSupplier inbox;
	@Mock EndpointFactory inboxSupplierFactory;
	@Mock WorkerEndpointMessageListener listener;
	@Mock Config mockedConfig;

	@Before
	public void injectMocks(){
		MockitoAnnotations.initMocks(this);
		module = spy( module );
		endpointContext.config = config;
		endpointContext.factories = asList( inboxSupplierFactory );
		doReturn( true ).when( inboxSupplierFactory ).canHandleEndpoint( anyString() );
		doReturn( inbox ).when( inboxSupplierFactory ).createSupplier( anyString(), anyString() );
	}

	@Test
	public void ensureThatCanDeployAllMessageConsumers() throws IOException {
		module.consumers = asList( first, second );
		module.load( null, null );
		verify( module, times(2) ).deploy( any(WorkerEndpointMessageListener.class), eq(1) );
	}

	@Test
	public void ensureThatDeployedMessageConsumerHaveItsParallelismConfigurationRead() throws IOException {
		doReturn( 1 ).when( mockedConfig ).getInteger( anyString(), anyInt() );
		endpointContext.config = mockedConfig;
		module.consumers = asList( first, second );
		module.load( null, null );
		verify( module, times(2) ).deploy( any(WorkerEndpointMessageListener.class), eq(1) );
		verify( endpointContext.config ).getInteger( eq( "server.uworkers.first.parallelism" ), eq(1) );
		verify( endpointContext.config ).getInteger( eq( "server.uworkers.second.parallelism" ), eq(1) );
	}

	@Test
	public void ensureThatUsesDefaultParallelismWhenNoDataIsAvailableForAGivenNamedConsumer() throws IOException {
		module.consumers = asList( notConfigured );
		module.load( null, null );
		verify( module ).runInBackgroundWithParallelism( any(EndpointInboxConsumer.class), eq(1) );
	}

	@Test
	public void ensureThatUsesItsOwnParallelismWhenItIsConfiguredOnTheConfigurationFile() throws IOException {
		module.consumers = asList( configured );
		module.load( null, null );
		verify( module ).runInBackgroundWithParallelism( any(EndpointInboxConsumer.class), eq(2) );
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