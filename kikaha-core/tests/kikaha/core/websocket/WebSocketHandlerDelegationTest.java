package kikaha.core.websocket;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xnio.ChannelListener.Setter;

/**
 * Scope: Ensure that is possible to wrap a {@link WebSocketHandler} object and
 * delegate all WebSocket events to it.
 *
 * @author Miere Teixeira
 */
@RunWith( MockitoJUnitRunner.class )
public class WebSocketHandlerDelegationTest {

	@Mock
	Setter<WebSocketChannel> setter;

	@Mock
	WebSocketHandler delegated;

	@Mock
	WebSocketHttpExchange exchange;

	@Mock
	WebSocketChannel channel;

	WebSocketConnectionCallback callbackHandler;

	@Before
	public void setup() {
		doReturn( "/websocket" ).when( exchange ).getRequestURI();
		doReturn( setter ).when( channel ).getReceiveSetter();
		callbackHandler = new WebSocketConnectionCallbackHandler( delegated );
	}

	@Test
	public void ensureThatCanDelegateOnOpenEventToWebSocketHandlerObject() {
		callbackHandler.onConnect( exchange, channel );
		verify( delegated ).onOpen( eq( channel ), eq( exchange ) );
		verify( setter ).set( isA( DelegatedReceiveListener.class ) );
	}

	@Test
	@SneakyThrows
	public void ensureThatCanDeleteOnTextEventToWebSocketHandlerObject() {
		final BufferedTextMessage message = mock( BufferedTextMessage.class );
		final DelegatedReceiveListener listener = new DelegatedReceiveListener( delegated );
		listener.onFullTextMessage( channel, message );
		verify( delegated ).onText( eq( channel ), eq( message ) );
	}

	@Test
	public void ensureThatCanDeleteOnCloseEventToWebSocketHandlerObject() {
		final CloseMessage message = mock( CloseMessage.class );
		final DelegatedReceiveListener listener = new DelegatedReceiveListener( delegated );
		listener.onCloseMessage( message, channel );
		verify( delegated ).onClose( eq( channel ), eq( message ) );
	}
}