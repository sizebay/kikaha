package kikaha.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpServerExchange;

import java.util.ArrayList;
import java.util.List;

import kikaha.core.DefaultDeploymentContext;
import kikaha.core.DefaultRequestHookChain;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import kikaha.core.api.RequestHook;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.UndertowStandaloneException;
import lombok.Getter;

import org.junit.Test;

public class DefaultRequestHookChainTest {

	final BooleanRequestHook requestHook = new BooleanRequestHook();
	final DeploymentContext context = createDeploymentContext().register( requestHook );
	final DefaultRequestHookChain chain = spy( new DefaultRequestHookChain( null, context ) );
	final Runnable someThread = new Thread();

	@Test
	public void ensureThatCouldExecuteARequestHookInChain() throws UndertowStandaloneException {
		chain.executeNext();
		assertThat( requestHook.isExecuted(), is( true ) );
	}

	@Test
	public void ensureThatCouldExecuteRequestInIOThread() throws UndertowStandaloneException {
		doReturn( false ).when( chain ).isInIOThread();
		doNothing().when( chain ).dispatchInIOThread( someThread );
		chain.executeInIOThread( someThread );
		verify( chain ).dispatchInIOThread( someThread );
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
	public void execute( RequestHookChain chain, HttpServerExchange exchange ) throws UndertowStandaloneException {
		executed = true;
	}
}