package kikaha.urouting;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.protocol.http.HttpServerConnection;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import io.undertow.util.Protocols;

import java.io.IOException;
import java.nio.ByteBuffer;

import kikaha.core.test.KikahaRunner;
import kikaha.urouting.api.DefaultResponse;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Response;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.samples.TodoResource;
import kikaha.urouting.samples.TodoResource.Todo;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xnio.OptionMap;
import org.xnio.Pool;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;

import trip.spi.Provided;
import trip.spi.ServiceProviderException;

/**
 * {@link ResponseWriter} should respect the content type defined by
 * {@link Response#contentType}.
 *
 * @issue #34
 * @author Miere Teixeira
 */
@SuppressWarnings( "unchecked" )
@RunWith( KikahaRunner.class )
public class ResponseWriterShouldRespectResponseContentTypeBehaviorTest {

	final HeaderMap headerMap = new HeaderMap();
	final StreamConnection streamConnection = createStreamConnection();
	final OptionMap options = OptionMap.EMPTY;
	final Pool<ByteBuffer> pool = mock( Pool.class );
	final ServerConnection connection = new HttpServerConnection( streamConnection, null, null, options, 0 );
	final HttpServerExchange exchange = createHttpExchange();

	@Provided
	ResponseWriter writer;

	@Provided
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
	public void ensureBehavior() throws ServiceProviderException, RoutingException, IOException {
		doNothing().when( writer ).sendBodyResponse( any( HttpServerExchange.class ), any( String.class ), any( String.class ),
			any( Object.class ) );
		final Todo todo = new Todo( "Frankenstein" );
		final Response response = DefaultResponse.ok( todo ).contentType( Mimes.JSON );
		writer.write( exchange, response );
		verify( writer, atLeastOnce() ).sendContentTypeHeader( any( HttpServerExchange.class ), eq( Mimes.JSON ) );
		verify( writer, never() ).sendContentTypeHeader( any( HttpServerExchange.class ), eq( Mimes.PLAIN_TEXT ) );
	}
}