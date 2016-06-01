package kikaha.core.modules.smart;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import java.util.Arrays;
import io.undertow.server.HttpServerExchange;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link FilterHttpHandler}
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterHttpHandlerTest {

	final HttpServerExchange exchange = new HttpServerExchange(null, 0);

	@Mock
	Filter firstFilter;

	@Test
	public void ensureThatIsAbleToRunTheChain() throws Exception {
		final FilterChainFactory factory = new FilterChainFactory( Arrays.asList(firstFilter) );
		final FilterHttpHandler handler = new FilterHttpHandler(factory);
		handler.handleRequest( exchange );
		verify( firstFilter ).doFilter( eq(exchange), any() );
	}
}
