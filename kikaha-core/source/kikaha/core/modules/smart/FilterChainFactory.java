package kikaha.core.modules.smart;

import java.util.Iterator;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

/**
 * Store the available Filters and create on-demand {@link FilterChain}s.
 */
@RequiredArgsConstructor
public class FilterChainFactory {

	final Iterable<Filter> chainData;

	public FilterChain createFrom(HttpServerExchange exchange) {
		return new FilterChain( chainData.iterator(), exchange );
	}

	/**
	 * A FilterChain is an object provided by the container to the developer giving a view into
	 * the invocation chain of a filtered request for a resource. Filters use the FilterChain to invoke
	 * the next filter in the chain, or if the calling filter is the last filter in the chain, to invoke
	 * the resource at the end of the chain.
	 */
	@RequiredArgsConstructor
	public static class FilterChain {

		final Iterator<Filter> iterator;
		final HttpServerExchange exchange;

		public void runNext() throws Exception {
			if ( !iterator.hasNext() )
				throw new UnsupportedOperationException( "No more filters available in this FilterChain" );
			iterator.next().doFilter(exchange, this);
		}
	}
}
