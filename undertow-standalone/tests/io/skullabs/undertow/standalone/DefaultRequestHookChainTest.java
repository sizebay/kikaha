package io.skullabs.undertow.standalone;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import io.skullabs.undertow.standalone.api.*;
import io.undertow.server.HttpServerExchange;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import org.junit.Test;

public class DefaultRequestHookChainTest {

	@Test
	public void ensureThatCouldExecuteARequestHookInChain() throws UndertowStandaloneException {
		final BooleanRequestHook requestHook = new BooleanRequestHook();
		final DeploymentContext context = createDeploymentContext();
		context.register( requestHook );
		final DefaultRequestHookChain chain = new DefaultRequestHookChain( null, context );
		chain.executeNext();
		assertThat( requestHook.isExecuted(), is( true ) );
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