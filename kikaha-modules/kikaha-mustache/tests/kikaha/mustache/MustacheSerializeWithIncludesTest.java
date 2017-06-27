package kikaha.mustache;

import kikaha.config.Config;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

/**
 * Created by ronei.gebert on 27/06/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MustacheSerializeWithIncludesTest {

    @Mock Config config;

    @Spy @InjectMocks
    MustacheSerializer serializer;

    @Before
    public void configureMocks(){
        doReturn( "tests-resources/root-path" ).when( config ).getString( "server.static.location" );
        serializer.readConfiguration();
    }

    @Test
    public void ensureCanSerializeATemplateWithIncludes(){
        final MustacheResponse response = MustacheResponse.ok().templateName( "test-with-include" );
        final String serialized = serializer.serialize(response.entity()).replace( "\r\n", "\n" );
        assertEquals( "Before the include.\nThe include.\nAfter the include.", serialized );
    }

    @Test
    public void ensureCanSerializeATemplateWithIncludesOnASubFolder(){
        final MustacheResponse response = MustacheResponse.ok().templateName( "another-root-path/test-with-include" );
        final String serialized = serializer.serialize(response.entity()).replace( "\r\n", "\n" );
        assertEquals( "Before the include.\nThe include.\nAfter the include.", serialized );
    }

}
