package kikaha.urouting.serializers.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.util.Headers;
import kikaha.core.test.KikahaServerRunner;
import kikaha.urouting.api.Mimes;
import lombok.val;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * An integration test that ensure JSON Authentication Mechanism works.
 *
 * Created by ronei.gebert on 07/06/2017.
 */
@RunWith(KikahaServerRunner.class)
public class JSONAuthenticationIntegrationTest {

    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .followRedirects(false).build();

    @Test
    public void ensureCanReceiveASuccessMessageWhenHavePerformedAuthentication() throws IOException {
        val credentials = new JSONAuthenticationMechanism.JSONCredentials();
        credentials.setUsername( "username" );
        credentials.setPassword( "password" );

        val url = postJson( "http://localhost:9999/auth/verify", credentials );
        val response = client.newCall(url.build()).execute();
        assertEquals( 200, response.code() );
    }

    @Test
    public void ensureWillReceiveAFailureMessageWhenHaveNotPerformedAuthentication() throws IOException {
        val credentials = new JSONAuthenticationMechanism.JSONCredentials();
        credentials.setUsername( "bad-username" );
        credentials.setPassword( "bad-password" );

        val url = postJson( "http://localhost:9999/auth/verify", credentials );
        val response = client.newCall(url.build()).execute();
        assertEquals( 401, response.code() );
    }

    static Request.Builder postJson(String url, Object jsonObject ) throws JsonProcessingException {
        return new Request.Builder().url( url )
                .addHeader(Headers.CONTENT_TYPE_STRING, Mimes.JSON )
                .method( "POST", asJson( jsonObject ) );
    }

    static RequestBody asJson( Object jsonObject ) throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString( jsonObject );
        return RequestBody.create(JSON, json);
    }
}
