package kikaha.uworkers.core;

import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.test.KikahaRunner;
import kikaha.uworkers.api.Exchange;
import kikaha.uworkers.api.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import java.io.IOException;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@code MicroWorkersTaskDeploymentModule}.
 */
@RunWith(KikahaRunner.class)
public class MicroWorkersTaskDeploymentModuleIntegrationTest {

	final WorkerEndpointMessageListener first = new FirstWorkerEndpointMessageListener();
	final WorkerEndpointMessageListener second = new SecondWorkerEndpointMessageListener();
	final WorkerEndpointMessageListener configured = new ConfiguredWorkerEndpointMessageListener();
	final WorkerEndpointMessageListener notConfigured = new NotConfiguredWorkerEndpointMessageListener();

	@Inject MicroWorkersTaskDeploymentModule module;
	@Inject MicroWorkersContext microWorkersContext;
	@Inject Config config;

	@Mock DeploymentContext deploymentContext;
	@Mock EndpointInboxSupplier inbox;
	@Mock EndpointFactory inboxSupplierFactory;
	@Mock WorkerEndpointMessageListener listener;
	@Mock Config mockedConfig;

	@Before
	public void injectMocks(){
		MockitoAnnotations.initMocks(this);
		module = spy( module );
		microWorkersContext.config = config;
		doReturn( inbox ).when( inboxSupplierFactory ).createSupplier( any() );
	}

	@After
	public void shutdownModules(){
		module.unload();
	}

	@Test
	public void ensureThatCanDeployAllMessageConsumers() throws IOException {
		module.consumers = asList( first, second );
		module.load( null, deploymentContext );
		verify( module, times(2) ).deploy( eq(deploymentContext), any(WorkerEndpointMessageListener.class) );
	}

	@Test
	public void ensureThatDeployedMessageConsumerHaveItsOwnConfigurationRead() throws IOException {
		doReturn( 1 ).when( mockedConfig ).getInteger( anyString(), anyInt() );
		microWorkersContext.config = mockedConfig;
		module.consumers = asList( first, second );
		module.load( null, deploymentContext );
		verify( module, times(2) ).deploy( eq(deploymentContext), any(WorkerEndpointMessageListener.class) );
		verify( microWorkersContext.config ).getConfig( eq( "server.uworkers.first" ) );
		verify( microWorkersContext.config ).getConfig( eq( "server.uworkers.second" ) );
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

@Worker( value = "first" )
class FirstWorkerEndpointMessageListener implements WorkerEndpointMessageListener {

	@Override
	public void onMessage(Exchange exchange) throws Throwable {
		throw new UnsupportedOperationException("onMessage not implemented yet!");
	}
}

@Worker( value = "second" )
class SecondWorkerEndpointMessageListener implements WorkerEndpointMessageListener {

	@Override
	public void onMessage(Exchange exchange) throws Throwable {
		throw new UnsupportedOperationException("onMessage not implemented yet!");
	}
}

@Worker( value = "configured" )
class ConfiguredWorkerEndpointMessageListener implements WorkerEndpointMessageListener {

	@Override
	public void onMessage(Exchange exchange) throws Throwable {
		throw new UnsupportedOperationException("onMessage not implemented yet!");
	}
}

@Worker( value = "not-configured" )
class NotConfiguredWorkerEndpointMessageListener implements WorkerEndpointMessageListener {

	@Override
	public void onMessage(Exchange exchange) throws Throwable {
		throw new UnsupportedOperationException("onMessage not implemented yet!");
	}
}