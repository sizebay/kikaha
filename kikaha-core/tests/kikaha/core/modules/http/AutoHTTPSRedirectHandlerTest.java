package kikaha.core.modules.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import kikaha.core.HttpServerExchangeStub;
import kikaha.core.modules.http.ssl.AutoHTTPSRedirectHandler;
import lombok.SneakyThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AutoHTTPSRedirectHandlerTest {

	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Mock
	HttpHandler nextHandlerInChain;

	@Test
	@SneakyThrows
	public void ensureThatHaveRedirectedToHttps(){
		exchange.setRequestScheme("http");
		exchange.getRequestHeaders().put(Headers.HOST, "localhost:8081");
		exchange.setRelativePath("/redirect/test");

		final HttpHandler redirectHandler = new AutoHTTPSRedirectHandler(nextHandlerInChain);
		redirectHandler.handleRequest(exchange);

		verify(nextHandlerInChain, never() ).handleRequest(any());
		final String location = exchange.getResponseHeaders().getFirst(Headers.LOCATION);
		assertEquals( "https://localhost:8081/redirect/test", location );
	}

	@Test
	@SneakyThrows
	public void ensureThatHaveNotSendRedirectAndCalledNextHandler(){
		exchange.setRequestScheme("https");

		final HttpHandler redirectHandler = new AutoHTTPSRedirectHandler(nextHandlerInChain);
		redirectHandler.handleRequest(exchange);

		verify(nextHandlerInChain ).handleRequest(any());
		assertNull(exchange.getResponseHeaders().getFirst(Headers.LOCATION));
	}
}
