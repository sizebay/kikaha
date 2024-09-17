package kikaha.mustache;

import kikaha.core.test.KikahaServerRunner;
import lombok.SneakyThrows;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by ronei.gebert on 27/06/2017.
 */
@RunWith(KikahaServerRunner.class)
public class MustacheSampleResourceITest {

    @Test
    @SneakyThrows
    public void ensureCanRenderTemplate(){
        final Request.Builder url = Http.url("http://localhost:9000/sample/mustache");
        final Response response = Http.send(url);
        assertEquals( 200, response.code() );
        final String responseBody = response.body().string().replace( "\r\n", "\n" );
        assertEquals( "Before the include.\nThe include.\nAfter the include.", responseBody );
    }

    @Test
    @SneakyThrows
    public void ensureCanRenderTemplateInAnotherRootPath(){
        final Request.Builder url = Http.url("http://localhost:9000/sample/mustache-subfolder");
        final Response response = Http.send(url);
        assertEquals( 200, response.code() );
        final String responseBody = response.body().string().replace( "\r\n", "\n" );
        assertEquals( "Before the include.\nThe include.\nAfter the include.", responseBody );
    }
}
