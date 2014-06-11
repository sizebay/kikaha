package io.skullabs.undertow.standalone;

import io.skullabs.undertow.standalone.api.DeploymentContext;
import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultHttpRequestHandler implements HttpHandler {
	
	final DeploymentContext context;

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		if ( exchange.isInIoThread() )
			exchange.dispatch(this);
		else
			createAndExecuteRequestChain(exchange);
	}

	private void createAndExecuteRequestChain(HttpServerExchange exchange) throws UndertowStandaloneException {
		exchange.addDefaultResponseListener(new IgnoreUnhandledExchangesResponseListener());
		RequestHookChain chain = new DefaultRequestHookChain( exchange, context );
		chain.executeNext();
	}

	private class IgnoreUnhandledExchangesResponseListener implements DefaultResponseListener {
		@Override
		public boolean handleDefaultResponse(HttpServerExchange exchange) {
			return true;
		}
	}
}
