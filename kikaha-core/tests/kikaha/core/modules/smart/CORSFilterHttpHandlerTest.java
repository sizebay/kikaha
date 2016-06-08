package kikaha.core.modules.smart;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static kikaha.core.modules.smart.CORSFilterHttpHandler.*;
import java.net.URL;
import javax.inject.Inject;
import io.undertow.server.*;
import io.undertow.util.*;
import kikaha.core.test.KikahaRunner;
import lombok.SneakyThrows;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class CORSFilterHttpHandlerTest {

	static final String LOCALHOST = "http://localhost";
	static final String UNKNOWN = "http://unknown.localhost";
	static final String LOCALHOST_9000 = "http://localhost:9000";

	@Mock
	HttpHandler nextHandler;

	@Mock
	HttpHandler notFoundHandler;

	@Spy
	ServerConnection connection;

	HttpHandler corsFilter;

	CORSConfig corsConfig;

	@Inject
	CORSFilterModule module;

	@Before
	public void createFilter(){
		MockitoAnnotations.initMocks(this);
		corsConfig = module.loadCorsConfig();
		corsFilter = new CORSFilterHttpHandler( corsConfig, nextHandler, notFoundHandler );
	}

	@Test
	public void ensureThatIsAbleToHandleCORSForExpectedMethodAndAllOriginFromConfig() throws Exception {
		final String expectedMethod = "GET";
		final HttpServerExchange exchange = createExchange(expectedMethod, UNKNOWN);
		exchange.setRequestMethod( Methods.OPTIONS );
		corsConfig.alwaysAllowOrigin = true;

		corsFilter.handleRequest( exchange );

		verify( nextHandler, never() ).handleRequest( eq( exchange ) );
		verify( notFoundHandler, never() ).handleRequest( eq( exchange ) );
		assertEquals( UNKNOWN, exchange.getResponseHeaders().get( ALLOWED_ORIGIN ).getFirst() );
	}

	@Test
	public void ensureThatSendExpectedHeadersBackOnAllowedCORSRequests() throws Exception {
		final String expectedMethod = "GET";
		final HttpServerExchange exchange = createExchange( expectedMethod, LOCALHOST );
		exchange.setRequestMethod( Methods.OPTIONS );
		exchange.getRequestHeaders().put(ACCESS_HEADERS, "Bla, Content-Type");

		corsFilter.handleRequest( exchange );

		verify( nextHandler, never() ).handleRequest( eq( exchange ) );
		verify( notFoundHandler, never() ).handleRequest( eq( exchange ) );
		assertEquals( LOCALHOST, exchange.getResponseHeaders().get( ALLOWED_ORIGIN ).getFirst() );
		assertEquals( "Bla, Content-Type", exchange.getResponseHeaders().get( ALLOWED_HEADERS ).getFirst() );
	}

	@Test
	public void ensureThatIsAbleToHandleCORSForExpectedMethodAndExpectedOriginFromConfig() throws Exception {
		final String expectedMethod = "GET";
		final HttpServerExchange exchange = createExchange( expectedMethod, LOCALHOST );
		exchange.setRequestMethod( Methods.OPTIONS );

		corsFilter.handleRequest( exchange );

		verify( nextHandler, never() ).handleRequest( eq( exchange ) );
		verify( notFoundHandler, never() ).handleRequest( eq( exchange ) );
		assertEquals( LOCALHOST, exchange.getResponseHeaders().get( ALLOWED_ORIGIN ).getFirst() );
	}

	@Test
	public void ensureThatIsNotAbleToHandleCORSForExpectedMethodButUnknownOrigin() throws Exception {
		final String expectedMethod = "GET";
		final HttpServerExchange exchange = createExchange(expectedMethod, UNKNOWN);
		exchange.setRequestMethod( Methods.OPTIONS );

		corsFilter.handleRequest( exchange );

		verify( nextHandler, never() ).handleRequest( eq( exchange ) );
		verify( notFoundHandler ).handleRequest( eq( exchange ) );
		assertNull( exchange.getResponseHeaders().get( ALLOWED_ORIGIN ) );
	}

	@Test
	public void ensureThatIsAbleToHandleCORSForExpectedMethodAndAllOriginAndOriginHostWithPort() throws Exception {
		final String expectedMethod = "GET";
		final HttpServerExchange exchange = createExchange(expectedMethod, LOCALHOST_9000);
		exchange.setRequestMethod( Methods.OPTIONS );
		corsConfig.alwaysAllowOrigin = true;

		corsFilter.handleRequest( exchange );

		verify( nextHandler, never() ).handleRequest( eq( exchange ) );
		verify( notFoundHandler, never() ).handleRequest( eq( exchange ) );
		assertEquals( LOCALHOST_9000, exchange.getResponseHeaders().get( ALLOWED_ORIGIN ).getFirst() );
	}

	@Test
	public void ensureThatOptionMethodWithoutTheRequiredHeaderIsRedirectedToNotFound() throws Exception {
		final String expectedMethod = "POST";
		final HttpServerExchange exchange = createExchange(expectedMethod, LOCALHOST);
		exchange.setRequestMethod( Methods.OPTIONS );

		corsFilter.handleRequest( exchange );

		verify( nextHandler, never() ).handleRequest( eq( exchange ) );
		verify( notFoundHandler ).handleRequest( eq( exchange ) );
		assertNull( exchange.getResponseHeaders().get( ALLOWED_ORIGIN ) );
	}

	@Test
	public void ensureThatRequestsWithoutOptionMethodIsHandleAsNormalRequest() throws Exception {
		final String expectedMethod = "GET";
		final HttpServerExchange exchange = createExchange(expectedMethod, LOCALHOST);
		corsConfig.allowCredentials = true;

		corsFilter.handleRequest( exchange );

		verify( nextHandler ).handleRequest( eq( exchange ) );
		verify( notFoundHandler, never() ).handleRequest( eq( exchange ) );
		assertEquals( LOCALHOST, exchange.getResponseHeaders().get( ALLOWED_ORIGIN ).getFirst() );
		assertEquals( "true", exchange.getResponseHeaders().get( ALLOWED_CREDENTIALS ).getFirst() );
	}

	@Test
	public void ensureThatCORSResponsesAllowCredentialsIfConfiguredForThis() throws Exception {
		final String expectedMethod = "GET";
		final HttpServerExchange exchange = createExchange(expectedMethod, UNKNOWN);
		exchange.setRequestMethod( Methods.OPTIONS );
		corsConfig.alwaysAllowOrigin = true;
		corsConfig.allowCredentials = true;

		corsFilter.handleRequest( exchange );

		assertEquals( "true", exchange.getResponseHeaders().get( ALLOWED_CREDENTIALS ).getFirst() );
	}

	@Test
	public void ensureThatCORSResponsesDoesNotAllowCredentialsIfNotConfiguredForThis() throws Exception {
		final String expectedMethod = "GET";
		final HttpServerExchange exchange = createExchange(expectedMethod, UNKNOWN);
		exchange.setRequestMethod( Methods.OPTIONS );
		corsConfig.alwaysAllowOrigin = true;
		corsConfig.allowCredentials = false;

		corsFilter.handleRequest( exchange );

		assertNull( exchange.getResponseHeaders().get( ALLOWED_CREDENTIALS ) );
	}

	@SneakyThrows
	private HttpServerExchange createExchange( String expectedMethod, String referer ) {
		final HeaderMap headerValues = new HeaderMap();
		headerValues.add( Headers.ORIGIN, referer );
		headerValues.add( ACCESS_METHOD, expectedMethod );

		final HttpServerExchange exchange = new HttpServerExchange(connection, headerValues, new HeaderMap(), 0);
		exchange.setRequestScheme("http");
		exchange.getRequestHeaders().add( Headers.HOST, new URL( referer ).getAuthority() );
		return exchange;
	}
}
