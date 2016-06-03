package kikaha.core.modules.smart;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import io.undertow.server.*;
import io.undertow.util.*;
import kikaha.config.*;
import lombok.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class RewriteRequestHookTest {

	final Config config = ConfigLoader.loadDefaults();

	@Mock
	HttpHandler chain;

	@After
	@SneakyThrows
	public void ensureThatHaveDelegatedRequestToNextHookInTheChain()
	{
		verify( chain ).handleRequest( any( HttpServerExchange.class ) );
	}

	@Test
	@SneakyThrows
	public void ensureThatCouldRewriteVirtualHost()
	{
		HttpServerExchange exchange = createVirtualHostExchange( "customer.localdomain" );
		SmartRouteRule rule = new SmartRouteRule( "{subdomain}.localdomain", "/{path}", "/{subdomain}/{path}" );
		HttpHandler hook = RewriteRequestHttpHandler.from( rule, chain );
		hook.handleRequest(exchange);
		assertEquals( "/customer/documents", exchange.getRelativePath() );
	}

	@Test
	@SneakyThrows
	public void ensureThatCouldRewriteVirtualHostAtPort8080()
	{
		HttpServerExchange exchange = createVirtualHostExchange( "customer.localdomain:8080" );
		SmartRouteRule rule = new SmartRouteRule( "{subdomain}.localdomain", "/{path}", "/{subdomain}/{path}" );
		HttpHandler hook = RewriteRequestHttpHandler.from( rule, chain );
		hook.handleRequest(exchange);
		assertEquals( "/customer/documents", exchange.getRelativePath() );
	}

	HttpServerExchange createVirtualHostExchange( final String virtualHost )
	{
		val requestHeaders = new HeaderMap();
		requestHeaders.add( Headers.HOST, virtualHost );
		HttpServerExchange exchange = new HttpServerExchange( null, requestHeaders, null, 0 );
		exchange.setRelativePath( "/documents" );
		return exchange;
	}

	@Test
	@SneakyThrows
	public void ensureThatCouldRewritePath()
	{
		HttpServerExchange exchange = createPathDefinedExchange();
		String path = "/{domain}-{action}.jsp?id={id}";
		SmartRouteRule rule = new SmartRouteRule( "{virtualHost}", path, "/{domain}/{id}/{action}/" );
		HttpHandler hook = RewriteRequestHttpHandler.from( rule, chain );
		hook.handleRequest(exchange);
		assertEquals( "/user/1234/edit/", exchange.getRelativePath() );
	}

	HttpServerExchange createPathDefinedExchange()
	{
		HttpServerExchange exchange = new HttpServerExchange( null );
		exchange.setRelativePath( "/user-edit.jsp?id=1234" );
		return exchange;
	}
}
