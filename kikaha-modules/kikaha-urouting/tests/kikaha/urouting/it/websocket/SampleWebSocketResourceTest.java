package kikaha.urouting.it.websocket;

import static junit.framework.TestCase.assertEquals;
import static kikaha.urouting.it.websocket.SampleWebSocketResource.HELLO_WORLD;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import kikaha.core.test.KikahaServerRunner;
import kikaha.urouting.it.Http;
import kikaha.urouting.it.Http.WebSocket;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for SampleWebSocketResource.
 */
@RunWith( KikahaServerRunner.class )
public class SampleWebSocketResourceTest {

	@Inject SampleWebSocketResource webSocketResource;

	@Test(timeout = 90000)
	public void ensureWebSocketIsAbleToReceiveMessage() throws InterruptedException {
		final Request.Builder url = Http.url("http://localhost:19999/it/websocket");
		final WebSocket webSocket = Http.connect( url );
		assertTrue( webSocket.send( HELLO_WORLD ) );
		Thread.sleep( 100l );
		assertTrue( webSocketResource.opened );
		assertEquals( HELLO_WORLD, webSocketResource.message );
	}

	@Test(timeout = 1000)
	public void ensureWebSocketIsAbleToSendMessageBack(){
		final WebSocket webSocket = Http.connect( Http.url( "http://localhost:19999/it/websocket" ) );
		webSocket.send( HELLO_WORLD );
		final String received = webSocket.receive();
		assertEquals( HELLO_WORLD, received );
	}
}