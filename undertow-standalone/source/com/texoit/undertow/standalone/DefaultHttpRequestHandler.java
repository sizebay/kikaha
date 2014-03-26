package com.texoit.undertow.standalone;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

import com.texoit.undertow.standalone.api.DeploymentContext;
import com.texoit.undertow.standalone.api.RequestHookChain;
import com.texoit.undertow.standalone.api.UndertowStandaloneException;

@RequiredArgsConstructor
public class DefaultHttpRequestHandler implements HttpHandler {
	
	final DeploymentContext context;

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		if ( exchange.isInIoThread() ) {
			exchange.dispatch(this);
			return;
		}
		
		createAndExecuteRequestChain(exchange);
	}

	private void createAndExecuteRequestChain(HttpServerExchange exchange) throws UndertowStandaloneException {
		try {
			RequestHookChain chain = new DefaultRequestHookChain(
					context.requestHooks().iterator(), exchange, context );
			chain.executeNext();
		} finally {
//			if ( !exchange.isComplete() )
//				exchange.endExchange();
			System.out.println("Should I've flushed the exchange? " + (!exchange.isComplete()) );
		}
	}
}
