package kikaha.core.modules;

import io.undertow.server.HttpServerExchange;
import kikaha.core.HttpServerExchangeStub;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class NotFoundHandlerTest {

	@Inject
	NotFoundHandler handler;

	@Test
	public void ensureThatSent404Response() throws Exception {
		final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
		handler.handleRequest( exchange );
		assertEquals( 404, exchange.getStatusCode() );
	}
}
