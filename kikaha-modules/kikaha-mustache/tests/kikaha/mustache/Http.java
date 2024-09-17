package kikaha.mustache;

import kikaha.urouting.api.Mimes;
import lombok.experimental.Delegate;
import okhttp3.*;
import okio.BufferedSink;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author: miere.teixeira
 */
public interface Http {

	OkHttpClient client = new OkHttpClient()
			.newBuilder()
			.connectTimeout(3, TimeUnit.SECONDS)
			.readTimeout(3, TimeUnit.SECONDS)
			.writeTimeout(3, TimeUnit.SECONDS)
			.followRedirects(false).build();

	static Request.Builder url(String url) {
		return new Request.Builder().url( url );
	}

	static Response send(Request.Builder request) {
		try {
			return client.newCall( request.build() ).execute();
		} catch ( IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	static WebSocket connect(Request.Builder request) {
		final WebSocket wrappedWebSocket = new WebSocket();
		final okhttp3.WebSocket webSocket = client.newWebSocket( request.build(), wrappedWebSocket );
		wrappedWebSocket.webSocket = webSocket;
		return wrappedWebSocket;
	}

	class EmptyText extends RequestBody {

		@Override
		public MediaType contentType() {
			return MediaType.parse( Mimes.PLAIN_TEXT );
		}

		@Override
		public void writeTo( BufferedSink bufferedSink ) throws IOException {}
	}

	class WebSocket extends WebSocketListener implements okhttp3.WebSocket {

		@Delegate okhttp3.WebSocket webSocket;
		volatile String lastReceivedMessage;

		@Override
		public void onMessage( okhttp3.WebSocket webSocket, String text ) {
			lastReceivedMessage = text;
		}

		public String receive(){
			while ( lastReceivedMessage == null )
				LockSupport.parkNanos( this, 2l );
			return lastReceivedMessage;
		}
	}
}
