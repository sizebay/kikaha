package kikaha.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import kikaha.core.api.conf.Configuration;
import kikaha.core.api.conf.SSLConfiguration;
import kikaha.core.rewrite.AutoHTTPSRedirectHandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UndertowServerAutoHTTPSRedirectBehaviorTest {

	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Mock
	Configuration kikahaConf;

	@Mock
	SSLConfiguration sslConf;

	UndertowServer server;

	@Test
	public void ensureThatUndertowServerSetHTTPRedirectAsRootHandlerWhenConfigIsTrue(){
		doReturn(sslConf).when(kikahaConf).ssl();
		doReturn(true).when(sslConf).autoRedirectFromHttpToHttps();
		server = new UndertowServer(kikahaConf);
		final HttpHandler handler = server.loadDefaultHttpHandler();
		assertTrue( handler instanceof AutoHTTPSRedirectHandler );
	}

	@Test
	public void ensureThatUndertowServerDoesntSetHTTPRedirectAsRootHandlerWhenConfigIsFalse(){
		doReturn(sslConf).when(kikahaConf).ssl();
		doReturn(false).when(sslConf).autoRedirectFromHttpToHttps();
		server = new UndertowServer(kikahaConf);
		final HttpHandler handler = server.loadDefaultHttpHandler();
		assertFalse( handler instanceof AutoHTTPSRedirectHandler );
	}
}
