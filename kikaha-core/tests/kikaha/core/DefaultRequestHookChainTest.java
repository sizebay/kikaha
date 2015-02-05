package kikaha.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpServerExchange;

import java.util.ArrayList;
import java.util.List;

import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import kikaha.core.api.RequestHook;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.KikahaException;
import kikaha.core.impl.DefaultDeploymentContext;
import kikaha.core.impl.DefaultRequestHookChain;
import lombok.Getter;

import org.junit.Test;

public class DefaultRequestHookChainTest {

	final BooleanRequestHook requestHook = new BooleanRequestHook();
	final DeploymentContext context = createDeploymentContext().register( requestHook );
	final DefaultRequestHookChain chain = spy( new DefaultRequestHookChain( null, context ) );
	final Runnable someThread = spy( new Thread() );

	@Test
	public void ensureThatCouldExecuteARequestHookInChain() throws KikahaException {
		chain.executeNext();
		assertThat( requestHook.isExecuted(), is( true ) );
	}

	@Test
	public void ensureThatDispatchedToWorkerThread() throws KikahaException {
		doReturn( true ).when( chain ).isInIOThread();
		doNothing().when( chain ).dispatchToWorkerThread( someThread );
		chain.executeInWorkerThread( someThread );
		verify( chain ).dispatchToWorkerThread( someThread );
	}

	@Test
	public void ensureThatNotDispatchRequestToWorkerThread() throws KikahaException {
		doReturn( false ).when( chain ).isInIOThread();
		doNothing().when( chain ).dispatchToWorkerThread( someThread );
		chain.executeInWorkerThread( someThread );
		verify( chain, never() ).dispatchToWorkerThread( someThread );
		verify( someThread ).run();
	}

	private DeploymentContext createDeploymentContext() {
		final Iterable<DeploymentHook> deploymentshooks = new ArrayList<DeploymentHook>();
		final List<RequestHook> requesthooks = new ArrayList<RequestHook>();
		return new DefaultDeploymentContext( deploymentshooks, requesthooks );
	}
}

class BooleanRequestHook implements RequestHook {

	@Getter
	boolean executed;

	@Override
	public void execute( RequestHookChain chain, HttpServerExchange exchange ) throws KikahaException {
		executed = true;
	}
}