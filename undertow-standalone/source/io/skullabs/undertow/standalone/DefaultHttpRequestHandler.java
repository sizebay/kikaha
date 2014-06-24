package io.skullabs.undertow.standalone;

import io.skullabs.undertow.standalone.api.DeploymentContext;
import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultHttpRequestHandler implements HttpHandler {
	
	final DeploymentContext context;

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		RequestHookChain chain = new DefaultRequestHookChain( exchange, context );
		chain.executeNext();
	}
}