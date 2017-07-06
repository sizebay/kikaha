package kikaha.core.modules.http;

import static org.mockito.Mockito.*;
import java.io.IOException;

import io.undertow.server.handlers.resource.ResourceHandler;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class StaticResourceModuleTest {

	@Spy
	StaticResourceModule module;

	@Spy
	DeploymentContext context;

	@Mock
	Config config;

	@Mock
	Config staticConfig;

	@Before
	public void setupConfiguration(){
		doReturn(staticConfig).when(config).getConfig("server.static");
		module.config = config;
	}

	@Test
	public void ensureThatCanEnableStaticRouting() throws IOException {
		doReturn(true).when(staticConfig).getBoolean( eq("enabled") );
		doReturn( "" ).when(staticConfig).getString( eq("location") );

		module.load( null, context );
		verify( context ).fallbackHandler( any(ResourceHandler.class) );
		verify( staticConfig, times(2) ).getString( eq("location") );
	}

	@Test
	public void ensureThatCanDisableStaticRouting() throws IOException {
		doReturn(false).when(staticConfig).getBoolean( eq("enabled") );

		module.load( null, context );
		verify( context, never() ).fallbackHandler( any(ResourceHandler.class) );
		verify( staticConfig, never() ).getString( eq("location") );
	}
}
