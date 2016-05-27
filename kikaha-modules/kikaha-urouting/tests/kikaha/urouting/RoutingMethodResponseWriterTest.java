package kikaha.urouting;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.protocol.http.HttpServerConnection;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import io.undertow.util.Protocols;
import kikaha.core.test.KikahaRunner;
import kikaha.urouting.api.Header;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Response;
import kikaha.urouting.samples.TodoResource;
import kikaha.urouting.samples.TodoResource.Todo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
@SuppressWarnings("unchecked")
@RunWith( KikahaRunner.class )
public class RoutingMethodResponseWriterTest {

	final HeaderMap headerMap = new HeaderMap();
	final StreamConnection streamConnection = createStreamConnection();
	final OptionMap options = OptionMap.EMPTY;
	final Pool<ByteBuffer> pool = mock( Pool.class );
	final ServerConnection connection = new HttpServerConnection(streamConnection, null, null, options , 0);
	final HttpServerExchange exchange = createHttpExchange();

	@Inject
	RoutingMethodResponseWriter writer;
	@Inject TodoResource resource;

	@Before
	public void setup() {
		writer = spy( writer );
	}

	private HttpServerExchange createHttpExchange() {
		final HttpServerExchange httpServerExchange = new HttpServerExchange( connection, null, headerMap, 200 );
		httpServerExchange.setRequestMethod(new HttpString("GET"));
		httpServerExchange.setProtocol(Protocols.HTTP_1_1);
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
		when( streamConnection.getIoThread() ).thenReturn(ioThread);
		return streamConnection;
	}

	private ConduitStreamSinkChannel createSinkChannel() throws IOException {
		final StreamSinkConduit sinkConduit = mock( StreamSinkConduit.class );
		when( sinkConduit.write(any( ByteBuffer.class )) ).thenReturn(1);
		final ConduitStreamSinkChannel sinkChannel = new ConduitStreamSinkChannel(null, sinkConduit);
		return sinkChannel;
	}

	private ConduitStreamSourceChannel createSourceChannel() {
		final StreamSourceConduit sourceConduit = mock( StreamSourceConduit.class );
		final ConduitStreamSourceChannel sourceChannel = new ConduitStreamSourceChannel(null, sourceConduit);
		return sourceChannel;
	}

	@Test
	@SneakyThrows
	public void ensureThatCanCallPOSTResourceAndHaveItsHeadersAndStatusSentAsExpected() {
		doNothing().when(writer).sendBodyResponse( any(),any(),any(),any() );

		final Todo todo = new Todo( "Frankenstein" );
		final Response response = resource.persistTodo(todo);
		write(response);
		verify( writer ).sendStatusCode( any(), eq(201) );
		verify( writer, atLeastOnce() ).sendHeader( any(HeaderMap.class), any(Header.class), any(String.class) );
	}

	@SneakyThrows
	private void write(final Response response) {
		try {
			exchange.startBlocking();
			writer.write(exchange, Mimes.PLAIN_TEXT, response );
		} catch ( final NullPointerException cause ) {
			log.error( cause.getMessage(), cause );
		}
	}

	@Test
	@SneakyThrows
	public void ensureThatCanCallGETResourceAndHaveItsHeadersAndStatusSentAsExpected() {
		doNothing().when(writer).sendBodyResponse( any(),any(),any(),any() );

		final Todo todo = new Todo( "Frankenstein" );
		resource.persistTodo(todo);
		final Todo persistedTodo = resource.getTodo(todo.getId());
		write( persistedTodo );
		verify( writer ).sendStatusCode( any(), eq(200) );
		verify( writer, atLeastOnce() ).sendContentTypeHeader( any(), eq(Mimes.PLAIN_TEXT));
	}

	@SneakyThrows
	private void write(final Todo response) {
		try {
			exchange.startBlocking();
			writer.write(exchange, Mimes.PLAIN_TEXT, response );
		} catch ( final NullPointerException cause ) {
			log.error( cause.getMessage() );
		}
	}
}