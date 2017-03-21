package kikaha.urouting;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import java.io.IOException;
import javax.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import kikaha.core.test.*;
import kikaha.urouting.RoutingMethodResponseWriter;
import kikaha.urouting.api.*;
import kikaha.urouting.unit.samples.TodoResource.Todo;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 * {@link RoutingMethodResponseWriter} should respect the content type defined by
 * {@link Response#contentType}.
 *
 * @issue #34
 * @author Miere Teixeira
 */
@SuppressWarnings( "unchecked" )
@RunWith( KikahaRunner.class )
public class ResponseWriterShouldRespectRoutingMethodResponseContentTypeBehaviorTest {

	final HttpServerExchange exchange = createHttpExchange();
	@Inject RoutingMethodResponseWriter writer;

	@Before
	public void setup() {
		writer = spy( writer );
	}

	private HttpServerExchange createHttpExchange() {
		final HttpServerExchange httpServerExchange = HttpServerExchangeStub.createHttpExchange();
		httpServerExchange.setRequestMethod( new HttpString( "GET" ) );
		httpServerExchange.setProtocol( Protocols.HTTP_1_1 );
		return httpServerExchange;
	}

	@Test
	public void ensureBehavior() throws IOException {
		doNothing().when( writer ).sendBodyResponse( any( HttpServerExchange.class ), any( String.class ), any( String.class ),
			any( Object.class ) );
		final Todo todo = new Todo( "Frankenstein" );
		final Response response = DefaultResponse.ok( todo ).contentType( Mimes.JSON );
		writer.write( exchange, response );
		verify( writer, atLeastOnce() ).sendContentTypeHeader( any( HttpServerExchange.class ), eq( Mimes.JSON ) );
		verify( writer, never() ).sendContentTypeHeader( any( HttpServerExchange.class ), eq( Mimes.PLAIN_TEXT ) );
	}
}