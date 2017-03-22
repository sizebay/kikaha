package kikaha.urouting.it.params;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import kikaha.core.test.KikahaServerRunner;
import kikaha.urouting.it.Http;
import kikaha.urouting.it.Http.EmptyText;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author: miere.teixeira
 */
@RunWith( KikahaServerRunner.class )
public class QueryParametersIntegrationTest {

	final Request.Builder request = Http.url( "http://localhost:19999/it/parameters/query?id=12" );

	@Test
	public void ensureCanSendGet() throws IOException {
		final Response response = Http.send( this.request.get() );
		ensureHaveTheExpectedResponse( response );
	}

	@Test
	public void ensureCanSendPost() throws IOException {
		final Response response = Http.send( this.request.post( new EmptyText() ) );
		ensureHaveTheExpectedResponse( response );
	}

	@Test
	public void ensureCanSendPut() throws IOException {
		final Response response = Http.send( this.request.put( new EmptyText() ) );
		ensureHaveTheExpectedResponse( response );
	}

	@Test
	public void ensureCanSendPatch() throws IOException {
		final Response response = Http.send( this.request.patch( new EmptyText() ) );
		ensureHaveTheExpectedResponse( response );
	}

	@Test
	public void ensureCanSendDelete() throws IOException {
		final Response response = Http.send( this.request.delete() );
		ensureHaveTheExpectedResponse( response );
	}

	void ensureHaveTheExpectedResponse( final Response response ) throws IOException {
		assertEquals( 200, response.code() );
		assertEquals( 12, Integer.valueOf( response.body().string() ), 0 );
	}
}
