package kikaha.urouting.it.params;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.undertow.util.FileUtils;
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

	final Builder requestMultiWithFile = new Builder()
			.url( "http://localhost:19999/it/parameters/form/multi-with-file" );

	final RequestBody form = new FormBody.Builder().add( "id", "12" ).build();

	final RequestBody formWithFile = new MultipartBody.Builder()
			.setType( MultipartBody.FORM )
			.addFormDataPart( "file", "any.txt", RequestBody.create(MediaType.parse("text/plain"), new File("tests-resources/large-file.txt")) )
			.build();

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
	public void ensureCanSendMultiPartFiles() throws IOException {
		final Response response = Http.send( this.requestMultiWithFile.post( formWithFile ) );
		final String expectedResponse = FileUtils.readFile( new FileInputStream(new File("tests-resources/large-file.txt")));
		assertEquals( expectedResponse, response.body().string() );
		assertEquals( 200, response.code() );
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
