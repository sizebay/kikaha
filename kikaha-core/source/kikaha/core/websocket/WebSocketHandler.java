package kikaha.core.websocket;

import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.io.IOException;

public interface WebSocketHandler {

	public void onOpen( final WebSocketChannel channel , final WebSocketHttpExchange exchange  );

	public void onText( final WebSocketChannel channel , final BufferedTextMessage message  )
		throws IOException;

	public void onClose( final WebSocketChannel channel , final CloseMessage cm  );
}
