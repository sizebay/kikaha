package kikaha.cloud.aws.xray.it;

import com.amazonaws.xray.entities.TraceHeader;
import kikaha.core.test.KikahaServerRunner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by miere.teixeira on 16/06/2017.
 */
@RunWith(KikahaServerRunner.class)
public class SampleResourceIntegrationTest {

    final static String TRACE_ID = "Root=1-59440aea-229915288782207bac24d65e;";

    final OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .followRedirects(false).build();

    @Test
    public void ensureWillReceivedATraceIdEvenWhenNoTraceIdIsSendToBackend() throws IOException {
        final Request.Builder url = new Request.Builder().url("http://localhost:9000/api/sample/trace-id");
        final Response response = client.newCall( url.build() ).execute();
        assertEquals( 200, response.code() );
        final String responseAsString  = response.body().string();
        assertNotNull(responseAsString );
    }

    @Test
    public void ensureWillReceiveTheSentTraceId() throws IOException {
        final Request.Builder url = new Request.Builder().url("http://localhost:9000/api/sample/trace-id")
            .addHeader( TraceHeader.HEADER_KEY, TRACE_ID );
        final Response response = client.newCall( url.build() ).execute();
        assertEquals( 200, response.code() );
        final String responseAsString  = response.body().string();
        assertEquals( TRACE_ID, responseAsString );
    }
}