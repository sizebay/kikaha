package kikaha.urouting.it.context;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import kikaha.core.test.KikahaServerRunner;
import kikaha.urouting.it.Http;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author: miere.teixeira
 */
@RunWith( KikahaServerRunner.class )
public class ContextualParametersIntegrationTest {

	@Test
	public void ensureReceiveHeaderFromHttpServerExchange() throws IOException {
		final Request.Builder request = url( "http://localhost:19999/it/parameters/contextual/exchange" );
		final Response response = Http.send( request );
		assertEquals( 200, response.code() );
		assertEquals( "12", response.body().string() );
	}

	@Test
	public void ensureReceiveHeaderFromHttpServerExchangeAsynchronously() throws IOException {
		final Request.Builder request = url( "http://localhost:19999/it/parameters/contextual/exchange/async1" );
		final Response response = Http.send( request );
		assertEquals( 200, response.code() );
		assertEquals( "12", response.body().string() );
	}

	@Test
	public void ensureReceiveLoggedInUser() throws IOException {
		final Request.Builder request = url( "http://localhost:19999/it/parameters/contextual/account" );
		final Response response = Http.send( request );
		assertEquals( 200, response.code() );
		assertEquals( FixedAuthenticationMechanism.USERNAME, response.body().string() );
	}

	@Test
	public void ensureReceiveLoggedInUserFromSecurityContext() throws IOException {
		final Request.Builder request = url( "http://localhost:19999/it/parameters/contextual/security-context" );
		final Response response = Http.send( request );
		assertEquals( 200, response.code() );
		assertEquals( FixedAuthenticationMechanism.USERNAME, response.body().string() );
	}

	@Test
	public void ensureReceiveLoggedInUserFromSession() throws IOException {
		final Request.Builder request = url( "http://localhost:19999/it/parameters/contextual/session-context" );
		final Response response = Http.send( request );
		assertEquals( 200, response.code() );
		assertEquals( FixedAuthenticationMechanism.USERNAME, response.body().string() );
	}

	static Request.Builder url( String url ){
		return Http.url( url )
				.get()
				.addHeader( "id", "12" );
	}
}
