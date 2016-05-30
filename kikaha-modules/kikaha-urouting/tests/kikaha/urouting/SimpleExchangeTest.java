package kikaha.urouting;

import static org.junit.Assert.assertEquals;
import java.net.URL;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import lombok.SneakyThrows;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleExchangeTest {

	@Mock
	RoutingMethodParameterReader parameterReader;

	@Mock
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

	@SneakyThrows
	private HttpServerExchange createExchange( String expectedMethod, String urlAsString ) {
		final URL url = new URL( urlAsString );

		final HeaderMap headerValues = new HeaderMap();
		headerValues.add( Headers.HOST, url.getAuthority() );

		final HttpServerExchange exchange = new HttpServerExchange(null, headerValues, new HeaderMap(), 0);
		exchange.setRequestMethod( new HttpString( expectedMethod ) );
		exchange.setRequestScheme( url.getProtocol() );
		exchange.setRelativePath( url.getPath() );
		return exchange;
	}
}
