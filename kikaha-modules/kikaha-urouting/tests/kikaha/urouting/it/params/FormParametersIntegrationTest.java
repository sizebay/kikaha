package kikaha.urouting.it.params;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import kikaha.core.test.KikahaServerRunner;
import kikaha.urouting.it.Http;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author: miere.teixeira
 */
@RunWith( KikahaServerRunner.class )
public class FormParametersIntegrationTest {

	final Builder request = new Builder()
			.url( "http://localhost:19999/it/parameters/form" );

	final Builder requestMulti = new Builder()
			.url( "http://localhost:19999/it/parameters/form/multi" );

	final RequestBody form = new FormBody.Builder().add( "id", "12" ).build();

	@Test
	public void ensureCanSendPost() throws IOException {
		final Response response = Http.send( this.request.post( form ) );
		ensureHaveTheExpectedResponse( response );
	}

	@Test
	public void ensureCanSendMultiPart() throws IOException {
		final Response response = Http.send( this.requestMulti.post( form ) );
		ensureHaveTheExpectedResponse( response );
	}

	@Test
	public void ensureCanSendPut() throws IOException {
		final Response response = Http.send( this.request.put( form ) );
		ensureHaveTheExpectedResponse( response );
	}

	@Test
	public void ensureCanSendPatch() throws IOException {
		final Response response = Http.send( this.request.patch( form ) );
		ensureHaveTheExpectedResponse( response );
	}

	void ensureHaveTheExpectedResponse( final Response response ) throws IOException {
		assertEquals( "12", response.body().string() );
		assertEquals( 200, response.code() );
	}
}
