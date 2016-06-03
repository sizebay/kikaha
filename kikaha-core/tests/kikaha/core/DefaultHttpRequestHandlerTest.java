package kikaha.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;
import io.undertow.server.*;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultHttpRequestHandlerTest {

	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Spy
	DeploymentContext context;

	@Mock
	HttpHandler rootHandler;

	DefaultHttpRequestHandler handler;

	@Before
	public void configureHandlerMock() throws Exception {
		doNothing().when( rootHandler ).handleRequest( anyObject() );
		doReturn( rootHandler ).when( context ).rootHandler();
		handler = spy( new DefaultHttpRequestHandler( context ) );
	}

	@Test
	public void ensureThatFixUrlThatEndsWithSlash() throws Exception {
		exchange.setRelativePath( "/url/that/ends/with/slash/" );
		handler.handleRequest( exchange );
		assertEquals( "/url/that/ends/with/slash", exchange.getRelativePath() );
	}

	@Test
	public void ensureThatKeepIntactUrlThatDoesNotEndsWithSlash() throws Exception {
		exchange.setRelativePath( "/url/that/does/not/ends/with/slash" );
		handler.handleRequest( exchange );
		assertEquals( "/url/that/does/not/ends/with/slash", exchange.getRelativePath() );
	}

	@Test
	public void ensureThatKeepIntactUrlThatRepresentsTheRootURL() throws Exception {
		exchange.setRelativePath( "/" );
		handler.handleRequest( exchange );
		assertEquals( "/", exchange.getRelativePath() );
	}

	@Test
	public void ensureThatDefaultHandlerDelegatesToRootHandler() throws Exception {
		exchange.setRelativePath( "/url/that/does/not/ends/with/slash" );
		handler.handleRequest( exchange );
		verify( rootHandler ).handleRequest( eq( exchange ) );
	}
}
