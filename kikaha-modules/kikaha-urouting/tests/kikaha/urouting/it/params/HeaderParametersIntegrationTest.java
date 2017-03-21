package kikaha.urouting.it.params;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import kikaha.core.test.KikahaServerRunner;
import kikaha.urouting.it.Http;
import kikaha.urouting.it.Http.EmptyText;
import okhttp3.Request.Builder;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author: miere.teixeira
 */
@RunWith( KikahaServerRunner.class )
public class HeaderParametersIntegrationTest {

	final Builder request = new Builder()
			.url( "http://localhost:19999/it/parameters/header" )
			.addHeader( "id", "12" );

	@Test
	public void ensureCanSendGet() throws IOException {
		final Response response = Http.request( this.request.get().build() );
		ensureHaveTheExpectedResponse( response );
	}

	@Test
	public void ensureCanSendPost() throws IOException {
		final Response response = Http.request( this.request.post( new EmptyText() ).build() );
		ensureHaveTheExpectedResponse( response );
	}

	@Test
	public void ensureCanSendPut() throws IOException {
		final Response response = Http.request( this.request.put( new EmptyText() ).build() );
		ensureHaveTheExpectedResponse( response );
	}

	@Test
	public void ensureCanSendPatch() throws IOException {
		final Response response = Http.request( this.request.patch( new EmptyText() ).build() );
		ensureHaveTheExpectedResponse( response );
	}

	@Test
	public void ensureCanSendDelete() throws IOException {
		final Response response = Http.request( this.request.delete().build() );
		ensureHaveTheExpectedResponse( response );
	}

	void ensureHaveTheExpectedResponse( final Response response ) throws IOException {
		assertEquals( 200, response.code() );
		assertEquals( 12, Integer.valueOf( response.body().string() ), 0 );
	}
}
