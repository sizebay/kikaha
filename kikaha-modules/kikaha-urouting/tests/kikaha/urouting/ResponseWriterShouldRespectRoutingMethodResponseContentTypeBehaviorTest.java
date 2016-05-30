package kikaha.urouting;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.inject.Inject;
import io.undertow.server.*;
import io.undertow.server.protocol.http.HttpServerConnection;
import io.undertow.util.*;
import kikaha.core.test.KikahaRunner;
import kikaha.urouting.api.*;
import kikaha.urouting.samples.TodoResource;
import kikaha.urouting.samples.TodoResource.Todo;
import lombok.SneakyThrows;
import org.junit.*;
import org.junit.runner.RunWith;
import org.xnio.*;
import org.xnio.conduits.*;

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

	final HeaderMap headerMap = new HeaderMap();
	final StreamConnection streamConnection = createStreamConnection();
	final OptionMap options = OptionMap.EMPTY;
	final ServerConnection connection = new HttpServerConnection( streamConnection, null, null, options, 0 );
	final HttpServerExchange exchange = createHttpExchange();

	@Inject
	RoutingMethodResponseWriter writer;

	@Inject
	TodoResource resource;

	@Before
	public void setup() {
		writer = spy( writer );
	}

	private HttpServerExchange createHttpExchange() {
		final HttpServerExchange httpServerExchange = new HttpServerExchange( connection, null, headerMap, 200 );
		httpServerExchange.setRequestMethod( new HttpString( "GET" ) );
		httpServerExchange.setProtocol( Protocols.HTTP_1_1 );
		return httpServerExchange;
	}

	@SneakyThrows
	private StreamConnection createStreamConnection() {
		final StreamConnection streamConnection = mock( StreamConnection.class );
		final ConduitStreamSinkChannel sinkChannel = createSinkChannel();
		when( streamConnection.getSinkChannel() ).thenReturn( sinkChannel );
		final ConduitStreamSourceChannel sourceChannel = createSourceChannel();
		when( streamConnection.getSourceChannel() ).thenReturn( sourceChannel );
		final XnioIoThread ioThread = mock( XnioIoThread.class );
		when( streamConnection.getIoThread() ).thenReturn( ioThread );
		return streamConnection;
	}

	private ConduitStreamSinkChannel createSinkChannel() throws IOException {
		final StreamSinkConduit sinkConduit = mock( StreamSinkConduit.class );
		when( sinkConduit.write( any( ByteBuffer.class ) ) ).thenReturn( 1 );
		final ConduitStreamSinkChannel sinkChannel = new ConduitStreamSinkChannel( null, sinkConduit );
		return sinkChannel;
	}

	private ConduitStreamSourceChannel createSourceChannel() {
		final StreamSourceConduit sourceConduit = mock( StreamSourceConduit.class );
		final ConduitStreamSourceChannel sourceChannel = new ConduitStreamSourceChannel( null, sourceConduit );
		return sourceChannel;
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