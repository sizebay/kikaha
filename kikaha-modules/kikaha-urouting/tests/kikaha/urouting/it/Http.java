package kikaha.urouting.it;

import java.io.IOException;
import kikaha.urouting.api.Mimes;
import okhttp3.*;
import okio.BufferedSink;

/**
 * @author: miere.teixeira
 */
public interface Http {

	OkHttpClient client = new OkHttpClient();

	static okhttp3.Response request( okhttp3.Request request ) {
		try {
			return client.newCall( request ).execute();
		} catch ( IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	class EmptyText extends RequestBody {

		@Override
		public MediaType contentType() {
			return MediaType.parse( Mimes.PLAIN_TEXT );
		}

		@Override
		public void writeTo( BufferedSink bufferedSink ) throws IOException {}
	}
}
