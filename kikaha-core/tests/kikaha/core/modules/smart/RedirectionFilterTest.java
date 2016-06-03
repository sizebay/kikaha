package kikaha.core.modules.smart;

import static kikaha.core.test.HttpServerExchangeStub.createHttpExchange;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.net.URL;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import kikaha.core.modules.smart.FilterChainFactory.FilterChain;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link RedirectionFilter}
 */
@RunWith(MockitoJUnitRunner.class)
public class RedirectionFilterTest {

	@Mock
	FilterChain chain;

	@Test
	public void ensureThatDoFilterRequestBasedOnVirtualHostsAndPath() throws Exception {
		final HttpServerExchange exchange = createExchange("http://old.kikaha.com/target");
		final SmartRouteRule rule = new SmartRouteRule( "{virtualHost}.kikaha.com", "/{path}", "http://new.com/{virtualHost}?from={path}" );
		final Filter filter = RedirectionFilter.from( rule );
		filter.doFilter( exchange, chain );

		verify( chain, never() ).runNext();

		final String location = exchange.getResponseHeaders().get(new HttpString("Location")).getFirst();
		assertEquals( "http://new.com/old?from=target", location );
	}

	@Test
	public void ensureThatDoNotFilterThatDoesNoMatchPath() throws Exception {
		final HttpServerExchange exchange = createExchange("http://old.kikaha.com/target");
		final SmartRouteRule rule = new SmartRouteRule( "{virtualHost}.kikaha.com", "/sub/{path}", "http://new.com/{virtualHost}?from={path}" );
		final Filter filter = RedirectionFilter.from( rule );
		filter.doFilter( exchange, chain );

		verify( chain ).runNext();

		final HeaderValues location = exchange.getResponseHeaders().get(new HttpString("Location"));
		assertNull( location );
	}

	@Test
	public void ensureThatDoNotFilterThatDoesNoMatchVirtualHost() throws Exception {
		final HttpServerExchange exchange = createExchange("http://old.unknown.com/sub/target");
		final SmartRouteRule rule = new SmartRouteRule( "{virtualHost}.kikaha.com", "/sub/{path}", "http://new.com/{virtualHost}?from={path}" );
		final Filter filter = RedirectionFilter.from( rule );
		filter.doFilter( exchange, chain );

		verify( chain ).runNext();

		final HeaderValues location = exchange.getResponseHeaders().get(new HttpString("Location"));
		assertNull( location );
	}

	@SneakyThrows
	private HttpServerExchange createExchange(final String urlAsString )
	{
		final URL url = new URL( urlAsString );
		final HttpServerExchange exchange = createHttpExchange();
		exchange.getRequestHeaders().add( Headers.HOST, url.getAuthority() );
		exchange.setRelativePath( url.getPath() );
		return exchange;
	}
}