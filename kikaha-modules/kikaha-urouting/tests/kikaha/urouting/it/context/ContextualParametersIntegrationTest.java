package kikaha.urouting.it.context;

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
public class ContextualParametersIntegrationTest {

	@Test
	public void ensureReceiveHeaderFromHttpServerExchange() throws IOException {
		final Request request = request( "http://localhost:19999/it/parameters/contextual/exchange" );
		final Response response = Http.request( request );
		assertEquals( 200, response.code() );
		assertEquals( "12", response.body().string() );
	}

	@Test
	public void ensureReceiveLoggedInUser() throws IOException {
		final Request request = request( "http://localhost:19999/it/parameters/contextual/account" );
		final Response response = Http.request( request );
		assertEquals( 200, response.code() );
		assertEquals( FixedAuthenticationMechanism.USERNAME, response.body().string() );
	}

	@Test
	public void ensureReceiveLoggedInUserFromSecurityContext() throws IOException {
		final Request request = request( "http://localhost:19999/it/parameters/contextual/security-context" );
		final Response response = Http.request( request );
		assertEquals( 200, response.code() );
		assertEquals( FixedAuthenticationMechanism.USERNAME, response.body().string() );
	}

	@Test
	public void ensureReceiveLoggedInUserFromSession() throws IOException {
		final Request request = request( "http://localhost:19999/it/parameters/contextual/session-context" );
		final Response response = Http.request( request );
		assertEquals( 200, response.code() );
		assertEquals( FixedAuthenticationMechanism.USERNAME, response.body().string() );
	}

	static Request request( String url ){
		return new Builder()
				.url( url ).get()
				.addHeader( "id", "12" ).build();
	}
}
