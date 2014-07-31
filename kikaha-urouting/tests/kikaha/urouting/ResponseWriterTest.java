package kikaha.urouting;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
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

import kikaha.urouting.ResponseWriter;
import kikaha.urouting.api.Header;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Response;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.samples.TodoResource;
import kikaha.urouting.samples.TodoResource.Todo;
import lombok.SneakyThrows;

import org.junit.Test;
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

@SuppressWarnings("unchecked")
public class ResponseWriterTest extends TestCase {
	
	final HeaderMap headerMap = new HeaderMap();
	final StreamConnection streamConnection = createStreamConnection();
	final OptionMap options = OptionMap.EMPTY;
	final Pool<ByteBuffer> pool = mock( Pool.class );
	final ServerConnection connection = new HttpServerConnection(streamConnection, null, null, options , 0);
	final HttpServerExchange exchange = createHttpExchange();

	@Provided ResponseWriter writer;
	@Provided TodoResource resource;

	@Override
	public void setup() throws ServiceProviderException {
		super.setup();
		writer = spy( writer );
	}

	private HttpServerExchange createHttpExchange() {
		HttpServerExchange httpServerExchange = new HttpServerExchange( connection, null, headerMap, 200 );
		httpServerExchange.setRequestMethod(new HttpString("GET"));
		httpServerExchange.setProtocol(Protocols.HTTP_1_1);
		return httpServerExchange;
	}

	@SneakyThrows
	private StreamConnection createStreamConnection() {
		final StreamConnection streamConnection = mock( StreamConnection.class );
		ConduitStreamSinkChannel sinkChannel = createSinkChannel();
		when( streamConnection.getSinkChannel() ).thenReturn( sinkChannel );
		ConduitStreamSourceChannel sourceChannel = createSourceChannel();
		when( streamConnection.getSourceChannel() ).thenReturn( sourceChannel );
		XnioIoThread ioThread = mock( XnioIoThread.class );
		when( streamConnection.getIoThread() ).thenReturn(ioThread);
		return streamConnection;
	}

	private ConduitStreamSinkChannel createSinkChannel() throws IOException {
		StreamSinkConduit sinkConduit = mock( StreamSinkConduit.class );
		when( sinkConduit.write(any( ByteBuffer.class )) ).thenReturn(1);
		ConduitStreamSinkChannel sinkChannel = new ConduitStreamSinkChannel(null, sinkConduit);
		return sinkChannel;
	}

	private ConduitStreamSourceChannel createSourceChannel() {
		StreamSourceConduit sourceConduit = mock( StreamSourceConduit.class );
		ConduitStreamSourceChannel sourceChannel = new ConduitStreamSourceChannel(null, sourceConduit);
		return sourceChannel;
	}
	
	@Test
	public void ensure() throws ServiceProviderException, RoutingException, IOException{
		final Todo todo = new Todo( "Frankenstein" );
		Response response = resource.persistTodo(todo);
		write(response);
		verify( writer ).sendHeader( any(HeaderMap.class), any(Header.class), any(String.class) );
	}

	private void write(Response response) throws ServiceProviderException,
			RoutingException, IOException {
		try {
			writer.write(exchange, Mimes.JSON, response );
		} catch ( NullPointerException cause ) {
			cause.printStackTrace();
		}
	}
}