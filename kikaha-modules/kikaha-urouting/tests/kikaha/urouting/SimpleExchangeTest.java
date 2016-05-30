package kikaha.urouting;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javax.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import kikaha.core.test.KikahaRunner;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class SimpleExchangeTest {

	@Inject
	RoutingMethodParameterReader parameterReader;

	@Inject
	RoutingMethodResponseWriter responseWriter;

	@Test
	public void ensureThatIsPossibleToRetrieveHostAndPort(){
		final HttpServerExchange request = createExchange("POST", "http://server:80/hello");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		assertEquals( "server:80", exchange.getHostAndPort() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveHostAndPortOnRequestsOmittingTheDefaultPort(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		assertEquals( "server", exchange.getHostAndPort() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentProtocol(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		assertEquals( "http", exchange.getRequestScheme() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentHttpMethod(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		assertEquals( "POST", exchange.getHttpMethod().toString() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentRelativePath(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		assertEquals( "/hello", exchange.getRelativePath() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentQueryParameters(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello?q=1");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		final Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
		assertEquals( "1", queryParameters.get("q").getFirst() );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveASingleQueryParameter() throws IOException {
		final HttpServerExchange request = createExchange("POST", "http://server/hello?q=1");
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		assertEquals( 1l, exchange.getQueryParameter( "q", Long.TYPE ), 0 );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveTheCurrentPathParameters(){
		final HttpServerExchange request = createExchange("POST", "http://server/hello/123");
		simulatePathParameterRequest( request );

		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		final Map<String, String> queryParameters = exchange.getPathParameters();
		assertEquals( "123", queryParameters.get("id") );
	}

	@Test
	public void ensureThatIsPossibleToRetrieveASinglePathParameters() throws IOException {
		final HttpServerExchange request = createExchange("POST", "http://server/hello/123");
		simulatePathParameterRequest( request );

		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		assertEquals( 123l, exchange.getPathParameter( "id", Long.TYPE ), 0 );
	}

	private static void simulatePathParameterRequest( HttpServerExchange request ) {
		final HashMap<String, String> parameters = new HashMap<>();
		parameters.put("id", "123");
		final PathTemplateMatch templateMatch = new PathTemplateMatch("/hello/{id}", parameters);
		request.putAttachment( PathTemplateMatch.ATTACHMENT_KEY, templateMatch );
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
}
