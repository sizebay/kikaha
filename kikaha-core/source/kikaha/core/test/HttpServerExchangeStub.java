package kikaha.core.test;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.ByteBuffer;
import io.undertow.io.Sender;
import io.undertow.server.*;
import io.undertow.server.protocol.http.HttpServerConnection;
import io.undertow.util.*;
import lombok.SneakyThrows;
import org.mockito.*;
import org.xnio.*;
import org.xnio.conduits.*;

public abstract class HttpServerExchangeStub {

	public static HttpServerExchange createHttpExchange() {
		final HttpServerExchange httpExchange = newHttpExchange();

		final Sender sender = mock( Sender.class );
		final Exposed exposed = new Exposed( httpExchange );
		exposed.setFieldValue( "sender", sender );

		return httpExchange;
	}

	private static HttpServerExchange newHttpExchange() {
		final HttpServerExchange httpServerExchange = new HttpServerExchange( null, new HeaderMap(), new HeaderMap(), 200 );
		httpServerExchange.setRequestMethod( new HttpString( "GET" ) );
		httpServerExchange.setProtocol( Protocols.HTTP_1_1 );
		httpServerExchange.setRelativePath("/test");
		return httpServerExchange;
	}
}
