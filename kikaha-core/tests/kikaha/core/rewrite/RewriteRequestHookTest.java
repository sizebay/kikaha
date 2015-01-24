package kikaha.core.rewrite;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import trip.spi.Provided;
import trip.spi.ServiceProvider;

@RunWith( MockitoJUnitRunner.class )
public class RewriteRequestHookTest {

	@Mock
	RequestHookChain chain;

	@Provided
	Configuration configuration;

	@Before
	@SneakyThrows
	public void provideDependencies()
	{
		val provider = new ServiceProvider();
		provider.providerFor( Configuration.class, DefaultConfiguration.loadDefaultConfiguration() );
		provider.provideOn( this );
	}

	@After
	@SneakyThrows
	public void ensureThatHaveDelegatedRequestToNextHookInTheChain()
	{
		verify( chain ).executeNext();
	}

	@Test
	@SneakyThrows
	public void ensureThatCouldRewriteVirtualHost()
	{
		val exchange = createVirtualHostExchange( "customer.localdomain" );
		val hook = new RewriteRequestHook( "{subdomain}.localdomain", "/{path}", "/{subdomain}/{path}" );
		hook.execute( chain, exchange );
		assertEquals( "/customer/documents", exchange.getRelativePath() );
	}

	@Test
	@SneakyThrows
	public void ensureThatCouldRewriteVirtualHostAtPort8080()
	{
		val exchange = createVirtualHostExchange( "customer.localdomain:8080" );
		val hook = new RewriteRequestHook( "{subdomain}.localdomain", "/{path}", "/{subdomain}/{path}" );
		hook.execute( chain, exchange );
		assertEquals( "/customer/documents", exchange.getRelativePath() );
	}

	HttpServerExchange createVirtualHostExchange( final String virtualHost )
	{
		val requestHeaders = new HeaderMap();
		requestHeaders.add( Headers.HOST, virtualHost );
		val exchange = new HttpServerExchange( null, requestHeaders, null, 0 );
		exchange.setRelativePath( "/documents" );
		return exchange;
	}

	@Test
	@SneakyThrows
	public void ensureThatCouldRewritePath()
	{
		val exchange = createPathDefinedExchange();
		val path = "/{domain}-{action}.jsp?id={id}";
		val hook = new RewriteRequestHook( "{virtualHost}", path, "/{domain}/{id}/{action}/" );
		hook.execute( chain, exchange );
		assertEquals( "/user/1234/edit/", exchange.getRelativePath() );
	}

	HttpServerExchange createPathDefinedExchange()
	{
		val exchange = new HttpServerExchange( null );
		exchange.setRelativePath( "/user-edit.jsp?id=1234" );
		return exchange;
	}

	@Test
	@SneakyThrows
	public void ensureThatRewriteApplingRuleFoundInConfigurationFile()
	{
		val exchange = createPathDefinedExchange();
		val rewriteRoute = configuration.routes().rewriteRoutes().get( 4 );
		val hook = new RewriteRequestHook( rewriteRoute );
		hook.execute( chain, exchange );
		assertEquals( "/user/1234/edit/", exchange.getRelativePath() );
	}

}
