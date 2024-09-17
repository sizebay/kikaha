package kikaha.rocker;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import javax.inject.Inject;
import com.fizzed.rocker.runtime.RockerRuntime;
import io.undertow.server.HttpServerExchange;
import kikaha.core.test.*;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith( KikahaRunner.class )
public class RockerTest {

    static private final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

    @Inject
    RockerSerializer serializer;

    @Before
    public void provideDependencies() {
        //enable rocker reloading bootstrap
        RockerRuntime.getInstance().setReloading(true);
    }

    // RockerTemplate
    @Test
    public void testRockerTemplate() {
        RockerTemplate rockerTemplate = new RockerTemplate();

        // set template name and objects.
        assertTrue(rockerTemplate.setObjects("Hello there") instanceof RockerTemplate);
        assertTrue(rockerTemplate.setTemplateName("index") instanceof RockerTemplate);

        // get template name.
        assertTrue( rockerTemplate.templateName instanceof String);
        assertThat( rockerTemplate.getTemplateName(), is( "index" ));

        // get objects.
        assertTrue( rockerTemplate.getObjects() instanceof Object);
        assertThat( rockerTemplate.getObjects(), is( new String[]{"Hello there"}));

        // Is rocker template?
        assertEquals(true, rockerTemplate instanceof RockerTemplate);

    }

    // RockerSerializerFactory
    @Test
    public void testRockerSerializerFactory() {
        RockerSerializerFactory rockerSerializerFactory = new RockerSerializerFactory();
        rockerSerializerFactory.serializer();

        assertEquals(true, rockerSerializerFactory instanceof RockerSerializerFactory);
    }

    // RockerResponse
    @Test
    public void testRockerResponse() {
        RockerResponse response = testSimulatedRockerResponse("Peter");

        assertEquals(true, response instanceof RockerResponse);
        assertTrue(response.entity.objects instanceof Object);
        assertThat( response.entity.templateName, is( "dummy.template" ) );
    }

    // RockerResponse simulated call.
    RockerResponse testSimulatedRockerResponse(String name) {
        User user = new User();
        user.name = name;
        RockerResponse response = new RockerResponse()
                .templateName( "dummy.template" )
                .objects( user );
        return response;
    }

}

