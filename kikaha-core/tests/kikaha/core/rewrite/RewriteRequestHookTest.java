package kikaha.core.rewrite;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import kikaha.core.api.RequestHookChain;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class RewriteRequestHookTest {

	@Mock
	RequestHookChain chain;

	@After
	@SneakyThrows
	public void ensureThatHaveDelegatedRequestToNextHookInTheChain() {
		verify( chain ).executeNext();
	}

	@Test
	@SneakyThrows
	public void ensureThatCouldRewriteVirtualHost() {
		val exchange = createVirtualHostExchange();
		val virtualHost = new VirtualHostPredicate( "{subdomain}.localdomain" );
		val hook = new RewriteRequestHook( virtualHost, "/{path}", "/{subdomain}/{path}" );
		hook.execute( chain, exchange );
		assertEquals( "/customer/documents", exchange.getRelativePath() );
	}

	HttpServerExchange createVirtualHostExchange() {
		val requestHeaders = new HeaderMap();
		requestHeaders.add( Headers.HOST, "customer.localdomain" );
		val exchange = new HttpServerExchange( null, requestHeaders, null, 0 );
		exchange.setRelativePath( "/documents" );
		return exchange;
	}
}
