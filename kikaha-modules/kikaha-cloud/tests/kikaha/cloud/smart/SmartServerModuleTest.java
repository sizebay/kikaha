package kikaha.cloud.smart;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.config.Config;
import kikaha.core.cdi.ServiceProvider;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link SmartServerModule}.
 */
@RunWith( MockitoJUnitRunner.class )
public class SmartServerModuleTest {

	@Mock Config config;
	@Mock ServiceRegistry serviceRegistry;
	@Mock ServiceProvider serviceProvider;

	@InjectMocks
	@Spy SmartServerModule module;

	@Before
	public void configureMocks(){
		doReturn( serviceRegistry ).when( serviceProvider ).load( serviceRegistry.getClass() );
	}

	@Test
	public void ensureCanExecuteTheServiceRegistry() throws IOException {
		doReturn( true ).when( config ).getBoolean( "server.smart-server.enabled" );
		doReturn( serviceRegistry.getClass() ).when( config ).getClass( anyString() );
		module.load( null, null );
		verify( serviceRegistry ).generateTheMachineId();
		verify( serviceRegistry ).registerCluster( any( ApplicationData.class ) );
	}

	@Test
	public void ensureWillNotStartModuleIfItIsNotEnabled() throws IOException {
		doReturn( false ).when( config ).getBoolean( "server.smart-server.enabled" );
		module.load( null, null );
		verify( serviceRegistry, never() ).generateTheMachineId();
		verify( serviceRegistry, never() ).registerCluster( any( ApplicationData.class ) );
	}

	@Test( expected = InstantiationError.class )
	public void ensureWillFailIfNoServiceRegistryIsAvailable() throws IOException {
		doReturn( true ).when( config ).getBoolean( "server.smart-server.enabled" );
		module.load( null, null );
	}
}