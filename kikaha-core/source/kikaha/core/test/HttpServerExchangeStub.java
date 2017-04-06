package kikaha.core.test;

import static org.mockito.Mockito.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import io.undertow.io.Sender;
import io.undertow.server.*;
import io.undertow.server.protocol.http.HttpServerConnection;
import io.undertow.util.*;
import kikaha.core.modules.security.*;
import lombok.SneakyThrows;
import org.mockito.*;
import org.xnio.*;
import org.xnio.conduits.*;

public abstract class HttpServerExchangeStub {

	public static HttpServerExchange createAuthenticatedHttpExchange(){
		final HttpServerExchange httpExchange = createHttpExchange();
		httpExchange.setSecurityContext( createSecurityContext() );
		return httpExchange;
	}

	public static HttpServerExchange createHttpExchange() {
		final ServerConnection serverConnection = createServerConnection();
		final HttpServerExchange httpExchange = newHttpExchange( serverConnection );
		final Sender sender = mock( Sender.class );
		final Exposed exposed = new Exposed( httpExchange );
		exposed.setFieldValue( "sender", sender );

		return httpExchange;
	}

	private static HttpServerExchange newHttpExchange( final ServerConnection serverConnection ) {
		final HttpServerExchange httpServerExchange = new HttpServerExchange( serverConnection, new HeaderMap(), new HeaderMap(), 200 );
		httpServerExchange.setRequestMethod( new HttpString( "GET" ) );
		httpServerExchange.setProtocol( Protocols.HTTP_1_1 );
		httpServerExchange.setRelativePath("/test");
		return httpServerExchange;
	}

	public static SecurityContext createSecurityContext(){
		final Session session = mock( Session.class );
		final SecurityContext securityContext = mock( SecurityContext.class );
		doReturn( session ).when( securityContext ).getCurrentSession();
		return securityContext;
	}

	private static ServerConnection createServerConnection(){
		final StreamConnection streamConnection = createStreamConnection();
		final OptionMap options = OptionMap.EMPTY;
		return new HttpServerConnection( streamConnection, null, null, options, 0, null );
	}

	@SneakyThrows
	private static StreamConnection createStreamConnection() {
		final StreamConnection streamConnection = Mockito.mock( StreamConnection.class );
		final ConduitStreamSinkChannel sinkChannel = createSinkChannel();
		Mockito.when( streamConnection.getSinkChannel() ).thenReturn( sinkChannel );
		final ConduitStreamSourceChannel sourceChannel = createSourceChannel();
		Mockito.when( streamConnection.getSourceChannel() ).thenReturn( sourceChannel );
		final XnioIoThread ioThread = Mockito.mock( XnioIoThread.class );
		Mockito.when( streamConnection.getIoThread() ).thenReturn( ioThread );
		return streamConnection;
	}

	private static ConduitStreamSinkChannel createSinkChannel() throws IOException {
		final StreamSinkConduit sinkConduit = Mockito.mock( StreamSinkConduit.class );
		Mockito.when( sinkConduit.write( Matchers.any( ByteBuffer.class ) ) ).thenReturn( 1 );
		final ConduitStreamSinkChannel sinkChannel = new ConduitStreamSinkChannel( null, sinkConduit );
		return sinkChannel;
	}

	private static ConduitStreamSourceChannel createSourceChannel() {
		final StreamSourceConduit sourceConduit = Mockito.mock( StreamSourceConduit.class );
		final ConduitStreamSourceChannel sourceChannel = new ConduitStreamSourceChannel( null, sourceConduit );
		return sourceChannel;
	}
}

