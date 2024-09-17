package kikaha.core.modules.http;

import kikaha.core.DeploymentContext;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by miere.teixeira on 21/12/2016.
 */
@RunWith(KikahaRunner.class)
public class HttpHandlerDeploymentModuleIntegrationTest {

    @Inject HttpHandlerDeploymentModule module;

    @Test
    public void ensureCanDeployHandlersEvenIfNoCustomizerIsAvailableOnTheClassPath() throws IOException {
        final DeploymentContext context = mock(DeploymentContext.class);
        final MyHandler myHandler = new MyHandler();
        module.handlers = Arrays.asList( myHandler );
        module.load( null, context );
        verify( context ).register( eq("/path"), eq("POST"), eq(myHandler) );
    }
}
