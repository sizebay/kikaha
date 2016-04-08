package kikaha.core.modules.http;

import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.http.StaticResourceModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

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
		doNothing().when( module ).setStaticRoutingAsFallbackHandler( anyObject(), eq(context) );
		doReturn( new File("") ).when( module ).retrieveWebAppFolder( eq(staticConfig) );

		module.load( null, context );
		verify( module ).setStaticRoutingAsFallbackHandler( anyObject(), eq(context) );
		verify( module ).retrieveWebAppFolder( eq(staticConfig) );
	}

	@Test
	public void ensureThatCanDisableStaticRouting() throws IOException {
		doReturn(false).when(staticConfig).getBoolean( eq("enabled") );

		module.load( null, context );
		verify( module, never() ).setStaticRoutingAsFallbackHandler( anyObject(), eq(context) );
		verify( module, never() ).retrieveWebAppFolder( eq(staticConfig) );
	}
}
