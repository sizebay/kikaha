package kikaha.core.modules.smart;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import io.undertow.server.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link HttpHandlerRunnerFilter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpHandlerRunnerFilterTest {

	final HttpServerExchange exchange = new HttpServerExchange(null, 0);

	@Mock
	HttpHandler handler;

	@Mock
	FilterChainFactory.FilterChain chain;

	@Test
	public void ensureThatRunTheHttpHandler() throws Exception {
		final HttpHandlerRunnerFilter runnerFilter = new HttpHandlerRunnerFilter( handler );
		runnerFilter.doFilter( exchange, chain );
		verify( handler ).handleRequest( eq( exchange ) );
	}

	@Test
	public void ensureThatDoesNotTheChainWhenTryToRunTheHttpHandler() throws Exception {
		final HttpHandlerRunnerFilter runnerFilter = new HttpHandlerRunnerFilter( handler );
		runnerFilter.doFilter( exchange, chain );
		verify( chain, never() ).runNext();
	}
}
