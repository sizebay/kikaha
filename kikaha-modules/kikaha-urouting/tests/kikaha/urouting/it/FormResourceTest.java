package kikaha.urouting.it;

import static org.junit.Assert.assertEquals;
import kikaha.core.test.KikahaServerRunner;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(KikahaServerRunner.class)
public class FormResourceTest {

	@Test
	public void ensureWillRedirectToLoginPageWhenNotCorrectlyAuthenticated(){
		final Request.Builder request = Http.url( "http://localhost:19999/it/form-auth/authenticated-only" );
		final Response response = Http.send( request );
		assertEquals( 303, response.code() );
		assertEquals( "/it/form-auth/login-page", response.header("Location") );
	}

	@Test
	public void ensureWillRedirectToSuccessPageWhenCorrectlyAuthenticated(){
		final RequestBody form = new FormBody.Builder()
				.add( "j_username", "admin" )
				.add( "j_password", "admin" ).build();
		final Request.Builder request = Http.url( "http://localhost:19999/it/form-auth/callback" ).post( form );
		final Response response = Http.send( request );
		assertEquals( 303, response.code() );
		assertEquals( "/", response.header("Location") );
	}

	@Test
	public void ensureDoesNotRequiresAuthenticationWhenAccessingLoginPage(){
		final Request.Builder request = Http.url( "http://localhost:19999/it/form-auth/login-page" ).get();
		final Response response = Http.send( request );
		assertEquals( 200, response.code() );
	}
}