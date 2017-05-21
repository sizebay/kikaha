package kikaha.core.modules.http;

import static org.mockito.Mockito.*;
import java.io.IOException;
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
		doNothing().when( module ).setStaticRoutingAsFallbackHandler( anyObject(), eq(context) );

		module.load( null, context );
		verify( module ).setStaticRoutingAsFallbackHandler( anyObject(), eq(context) );
		verify( staticConfig).getString( eq("location") );
	}

	@Test
	public void ensureThatCanDisableStaticRouting() throws IOException {
		doReturn(false).when(staticConfig).getBoolean( eq("enabled") );

		module.load( null, context );
		verify( module, never() ).setStaticRoutingAsFallbackHandler( anyObject(), eq(context) );
		verify( staticConfig, never() ).getString( eq("location") );
	}
}
