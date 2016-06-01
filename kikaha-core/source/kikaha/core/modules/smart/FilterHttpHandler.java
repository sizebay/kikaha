package kikaha.core.modules.smart;

import io.undertow.server.*;
import lombok.RequiredArgsConstructor;

/**
 * A {@link HttpHandler} that runs the {@link FilterChainFactory.FilterChain}.
 */
@RequiredArgsConstructor
public class FilterHttpHandler implements HttpHandler {

	final FilterChainFactory factory;

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		final FilterChainFactory.FilterChain chain = factory.createFrom(exchange);
		chain.runNext();
	}
}
