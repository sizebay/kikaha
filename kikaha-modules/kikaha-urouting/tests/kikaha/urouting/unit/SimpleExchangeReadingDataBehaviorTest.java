package kikaha.urouting.unit;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import kikaha.core.test.Exposed;
import kikaha.core.test.KikahaRunner;
import kikaha.urouting.*;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class SimpleExchangeReadingDataBehaviorTest {

	@Inject RoutingMethodParameterReader parameterReader;
	@Inject RoutingMethodResponseWriter responseWriter;
	@Inject RoutingMethodExceptionHandler exceptionHandler;

	@Test
	public void ensureThatIsPossibleToRetrieveHostAndPort(){
		final HttpServerExchange request = createExchange("POST", "http://server:80/hello");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		assertEquals( "server:80", exchange.getHostAndPort() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveHostAndPortOnRequestsOmittingTheDefaultPort(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		assertEquals( "server", exchange.getHostAndPort() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentProtocol(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		assertEquals( "http", exchange.getRequestScheme() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentHttpMethod(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		assertEquals( "POST", exchange.getHttpMethod().toString() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentRelativePath(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		assertEquals( "/hello", exchange.getRelativePath() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentQueryParameters(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello?q=1");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		final Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
		assertEquals( "1", queryParameters.get("q").getFirst() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveASingleQueryParameter() throws IOException {
		final HttpServerExchange request = createExchange("POST", "http://server/hello?q=1");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		assertEquals( 1l, exchange.getQueryParameter( "q", Long.TYPE ), 0 );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentPathParameters(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello/123");
		simulatePathParameterRequest( request );

		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		final Map<String, String> queryParameters = exchange.getPathParameters();
		assertEquals( "123", queryParameters.get("id") );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveASinglePathParameter() throws IOException {
		final HttpServerExchange request = createExchange("POST", "http://server/hello/123");
		simulatePathParameterRequest( request );

		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		assertEquals( 123l, exchange.getPathParameter( "id", Long.TYPE ), 0 );
	}

	private static void simulatePathParameterRequest( HttpServerExchange request ) {
		final HashMap<String, String> parameters = new HashMap<>();
		parameters.put("id", "123");
		final PathTemplateMatch templateMatch = new PathTemplateMatch("/hello/{id}", parameters);
		request.putAttachment( PathTemplateMatch.ATTACHMENT_KEY, templateMatch );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentHeaderParameters(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello/123");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		final HeaderMap queryParameters = exchange.getHeaderParameters();
		assertEquals( "server", queryParameters.get("HOST").getFirst() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveASingleHeaderParameter() throws IOException {
		final HttpServerExchange request = createExchange("POST", "http://server/hello/123");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		assertEquals( "server", exchange.getHeaderParameter( "HOST", String.class ) );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentCookieParameters(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello/123");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		final Map<String, Cookie> cookies = exchange.getCookieParameters();
		assertEquals( "1324", cookies.get("JSESSIONID").getValue() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveASingleCookieParameter() throws IOException {
		final HttpServerExchange request = createExchange("POST", "http://server/hello/123");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter, exceptionHandler );
		assertEquals( "1324", exchange.getCookieParameter( "JSESSIONID", String.class ) );
	}

	@SneakyThrows
	private HttpServerExchange createExchange( String expectedMethod, String urlAsString ) {
		final URL url = new URL( urlAsString );

		final HeaderMap headerValues = new HeaderMap();
		headerValues.add( Headers.HOST, url.getAuthority() );

		final HttpServerExchange exchange = new HttpServerExchange(null, headerValues, new HeaderMap(), 0);
		exchange.setRequestMethod( new HttpString( expectedMethod ) );
		exchange.setRequestScheme( url.getProtocol() );
		exchange.setRelativePath( url.getPath() );

		populateWithCookies( exchange );
		populateWithQueryString( exchange, url.toURI().getQuery() );

		return exchange;
	}

	private void populateWithQueryString( HttpServerExchange exchange, String queryString ){
		if ( queryString != null ) {
			for ( String query : queryString.split("&") ) {
				final String[] params = query.split("=");
				exchange.addQueryParam( params[0], params[1] );
			}
		}
	}

	private void populateWithCookies( HttpServerExchange exchange ){
		final Map<String, Cookie> cookies = new HashMap<>();
		cookies.put( "JSESSIONID", new CookieImpl( "JSESSIONID", "1324" ));
		new Exposed( exchange ).setFieldValue( "requestCookies", cookies );
	}
}
