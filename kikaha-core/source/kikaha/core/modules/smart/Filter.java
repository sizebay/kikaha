package kikaha.core.modules.smart;

import io.undertow.server.HttpServerExchange;

/**
 * A filter is an object that performs filtering tasks on either the request to a resource,
 * or on the response from a resource, or both.
 */
public interface Filter {

	/**
	 * Execute the filter logic.
	 *
	 * @param exchange object that represents the current request/response
	 * @param chain a chained list with all filters that wasn't executed.
	 */
	void doFilter(HttpServerExchange exchange, FilterChainFactory.FilterChain chain) throws Exception;
}
