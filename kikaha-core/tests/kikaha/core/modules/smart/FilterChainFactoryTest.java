package kikaha.core.modules.smart;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import io.undertow.server.HttpServerExchange;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * Unit tests for {@link FilterChainFactory}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterChainFactoryTest {

	final HttpServerExchange exchange = new HttpServerExchange(null, 0);

	@Mock
	Filter firstFilter;

	@Mock
	Filter secondFilter;

	FilterChainFactory factory;

	@Before
	public void loadFactory(){
		factory = new FilterChainFactory( Arrays.asList(firstFilter, secondFilter) );
	}

	@Test
	public void ensureThatIsAbleToCreateANewChain(){
		final FilterChainFactory.FilterChain chain = factory.createFrom( exchange );
		assertNotNull( chain );
	}

	@Test
	public void ensureThatIsAbleToCallTheFirstFilter() throws Exception {
		final FilterChainFactory.FilterChain chain = factory.createFrom( exchange );
		chain.runNext();
		verify( firstFilter ).doFilter( eq(exchange), eq( chain ) );
	}

	@Test
	public void ensureThatIsAbleToCallTheSecondFilterIfTheFirstOneExecuteTheNextItemOfChain() throws Exception {
		final FilterChainFactory.FilterChain chain = factory.createFrom( exchange );

		final Answer<Void> runNextItemOfChain = (a)-> { chain.runNext(); return null; };
		doAnswer( runNextItemOfChain ).when( firstFilter ).doFilter( eq(exchange), eq( chain ) );

		chain.runNext();
		verify( secondFilter ).doFilter( eq(exchange), eq( chain ) );
	}

	@Test(expected = UnsupportedOperationException.class)
	public void ensureThatIsNotPossibleToAskForTheNextItemOfTheChainMoreTimesThanIsAvailable() throws Exception {
		final FilterChainFactory.FilterChain chain = factory.createFrom( exchange );
		chain.runNext();
		chain.runNext();
		chain.runNext();
	}
}
