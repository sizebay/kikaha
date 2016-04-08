package kikaha.core.modules.http;

import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.http.HttpModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpModuleTest {

	@Mock
	Config config;

	@Mock
	Config httpConfig;

	@Mock
	DeploymentContext context;

	@Spy
	HttpModule module;

	@Before
	public void setup(){
		doNothing().when( module ).loadHttpListener( httpConfig, null );
		doReturn( httpConfig ).when( config ).getConfig( "server.http" );
		module.config = config;
	}

	@Test
	public void ensureThatCanLoadHttpListenerWhenItIsEnabled(){
		doReturn( true ).when( httpConfig ).getBoolean( "enabled" );
		module.load( null, context );
		verify( module ).loadHttpListener( eq( httpConfig ), anyObject() );
	}

	@Test
	public void ensureThatCanNotLoadHttpListenerWhenItIsDisabled(){
		doReturn( false ).when( httpConfig ).getBoolean( "enabled" );
		module.load( null, context );
		verify( module, never() ).loadHttpListener( eq( httpConfig ), anyObject() );
	}
}
