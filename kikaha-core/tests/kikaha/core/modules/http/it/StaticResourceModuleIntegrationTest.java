package kikaha.core.modules.http.it;

import kikaha.core.KikahaUndertowServer;
import kikaha.core.test.KikahaRunner;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by ronei.gebert on 06/07/2017.
 */
@RunWith(KikahaRunner.class)
public class StaticResourceModuleIntegrationTest {

    @Inject
    KikahaUndertowServer server;

    final OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .followRedirects(false).build();

    @Before
    public void configure(){
        System.setProperty( "server.http.port", "9001" );
    }

    @After
    public void shutdown(){
        server.stop();
    }

    @Test
    @SneakyThrows
    public void ensureCanFindIndexFileOnResourcesWhenAccessRootPath(){
        System.setProperty( "server.static.location", "webapp" );
        server.run();
        final Request.Builder url = new Request.Builder().url("http://localhost:9001/" ).get();
        final Response response = client.newCall( url.build() ).execute();
        assertEquals( 200, response.code() );
        final String responseAsString  = response.body().string();
        assertEquals( "<html><body>Index from WebApp</body></html>", responseAsString );
    }

    @Test
    @SneakyThrows
    public void ensureCanFindIndexFileOnClasspathWhenAccessRootPath(){
        System.setProperty( "server.static.location", "webapp-on-classpath" );
        server.run();
        final Request.Builder url = new Request.Builder().url("http://localhost:9001/" ).get();
        final Response response = client.newCall( url.build() ).execute();
        assertEquals( 200, response.code() );
        final String responseAsString  = response.body().string();
        assertEquals( "<html><body>Index from ClassPath</body></html>", responseAsString );
    }

}
