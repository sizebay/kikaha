package kikaha.core.modules.smart;

import io.undertow.server.*;
import lombok.RequiredArgsConstructor;

/**
 * A {@link Filter} implementation that runs {@link HttpHandler}.
 */
@RequiredArgsConstructor
public class HttpHandlerRunnerFilter implements Filter {

	final HttpHandler handler;

	@Override
	public void doFilter(HttpServerExchange exchange, FilterChainFactory.FilterChain chain) throws Exception {
		handler.handleRequest(exchange);
	}
}
