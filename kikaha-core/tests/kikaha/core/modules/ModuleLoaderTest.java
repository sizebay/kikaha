package kikaha.core.modules;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.Arrays;
import javax.inject.Inject;
import kikaha.core.DeploymentContext;
import kikaha.core.test.KikahaRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class ModuleLoaderTest {

	@Inject
	ModuleLoader loader;

	@Mock
	Module http;

	@Mock
	Module https;

	@Mock
	DeploymentContext context;

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		doReturn( "http" ).when(http).getName();
		doReturn( "https" ).when(https).getName();
		loader.modules = Arrays.asList( http, https );
	}

	@Test
	public void ensureThatCanLoadModules() throws IOException {
		loader.load( null, context );
		verify(http).load( anyObject(), eq( context ) );
		verify(https).load( anyObject(), eq( context ) );
	}

	@Test
	public void ensureThatCanUnloadModules() throws IOException {
		loader.unloadModules();
		verify(http).unload();
		verify(https).unload();
	}

	@Test
	public void ensureThatWouldShutdownTheGracefulShutdownListener() throws InterruptedException {
		loader.gracefulShutdownHandler = mock( GracefulShutdownHandler.class );
		loader.unloadModules();
		verify(loader.gracefulShutdownHandler).shutdown();
		verify(loader.gracefulShutdownHandler).awaitShutdown( eq(300000l) );
	}
}
