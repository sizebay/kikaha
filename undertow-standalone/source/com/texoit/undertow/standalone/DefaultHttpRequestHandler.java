package com.texoit.undertow.standalone;

import java.util.Iterator;

import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

import com.texoit.undertow.standalone.api.DeploymentContext;
import com.texoit.undertow.standalone.api.RequestHook;
import com.texoit.undertow.standalone.api.RequestHookChain;
import com.texoit.undertow.standalone.api.UndertowStandaloneException;

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
		try {
			exchange.addDefaultResponseListener(new IgnoreUnhandledExchangesResponseListener());
			Iterator<RequestHook> iterator = context.requestHooks().iterator();
			RequestHookChain chain = new DefaultRequestHookChain( iterator, exchange, context );
			chain.executeNext();
		} finally {
			System.out.println("Should I've flushed the exchange? " + (!exchange.isComplete()) );
		}
	}

	private class IgnoreUnhandledExchangesResponseListener implements DefaultResponseListener {

		@Override
		public boolean handleDefaultResponse(HttpServerExchange exchange) {
			return true;
		}
	}
}
