package kikaha.core.modules.smart;

import javax.inject.Singleton;
import io.undertow.server.HttpServerExchange;
import lombok.EqualsAndHashCode;

/**
 *
 */
@Singleton
@EqualsAndHashCode
public class EmptyFilter implements Filter {

	@Override
	public void doFilter(HttpServerExchange exchange, FilterChainFactory.FilterChain chain) throws Exception {
		chain.runNext();
	}
}
