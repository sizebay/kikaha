package kikaha.core.modules;

import kikaha.core.DeploymentContext;
import kikaha.core.test.KikahaRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

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
	}

	@Test
	public void ensureThatCanLoadModules() throws IOException {
		loader.modules = Arrays.asList( http, https );
		loader.load( null, context );
		verify(http).load( anyObject(), eq( context ) );
		verify(https).load( anyObject(), eq( context ) );
	}
}
