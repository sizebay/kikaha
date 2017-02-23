package kikaha.core.modules.smart;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.*;
import io.undertow.server.*;
import io.undertow.util.HeaderMap;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for StaticHeadersHttpHandler.
 */
@RunWith( MockitoJUnitRunner.class )
public class StaticHeadersHttpHandlerTest {

	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
	final Map<String, Object> headers = Collections.singletonMap( "Location", "/right/{url}" );
	final String urlMatcher = "/valid/{url}";

	@Mock HttpHandler httpHandler;

	@Test
	public void ensureThatDoNotSendHeadersWhenTheURLDoesNotMatch() throws Exception {
		exchange.setRelativePath( "/invalid/url" );

		final HttpHandler staticHeadersHttpHandler = StaticHeadersHttpHandler.create( httpHandler, urlMatcher, headers );
		staticHeadersHttpHandler.handleRequest( exchange );

		verify( httpHandler ).handleRequest( eq( exchange ) );

		final HeaderMap responseHeaders = exchange.getResponseHeaders();
		assertEquals( 0, responseHeaders.size() );
	}

	@Test
	public void ensureThatSendHeadersWhenTheURLMatches() throws Exception {
		exchange.setRelativePath( "/valid/here" );

		final HttpHandler staticHeadersHttpHandler = StaticHeadersHttpHandler.create( httpHandler, urlMatcher, headers );
		staticHeadersHttpHandler.handleRequest( exchange );

		verify( httpHandler ).handleRequest( eq( exchange ) );

		final HeaderMap responseHeaders = exchange.getResponseHeaders();
		assertEquals( 1, responseHeaders.size() );
		assertEquals( "/right/here", responseHeaders.getFirst( "Location" ) );
	}
}