package kikaha.core;

import kikaha.core.api.DeploymentContext;
import kikaha.core.api.RequestHookChain;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultHttpRequestHandler implements HttpHandler {

	final DeploymentContext context;

	@Override
	public void handleRequest( final HttpServerExchange exchange ) throws Exception {
		final RequestHookChain chain = new DefaultRequestHookChain( exchange, context );
		chain.executeNext();
	}
}