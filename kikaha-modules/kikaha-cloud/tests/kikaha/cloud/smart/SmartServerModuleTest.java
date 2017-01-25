package kikaha.cloud.smart;

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
	@Mock LocalAddressResolver localAddressResolver;

	@InjectMocks
	@Spy SmartServerModule module;

	@Before
	public void configureMocks() throws IOException {
		doReturn( localAddressResolver.getClass() ).when( config ).getClass( eq("server.smart-server.local-address.resolver") );
		doReturn( localAddressResolver ).when( serviceProvider ).load( eq(localAddressResolver.getClass()) );
		doReturn( "10.0.0.1" ).when( localAddressResolver ).getLocalAddress();
		doReturn( serviceRegistry ).when( serviceProvider ).load( eq(serviceRegistry.getClass()) );
		doReturn( true ).when( config ).getBoolean( eq("server.http.enabled") );
		doReturn( 9000 ).when( config ).getInteger( eq("server.http.port") );
	}

	@Test
	public void ensureCanExecuteTheServiceRegistry() throws IOException {
		provideDataNeededToExecuteTheModule();
		module.loadApplicationData();
		module.load( null, null );
		verify( serviceRegistry ).generateTheMachineId();
		verify( serviceRegistry ).registerIntoCluster( any( ApplicationData.class ) );
	}

	@Test
	public void ensureCanDeregisterTheService() throws IOException {
		provideDataNeededToExecuteTheModule();
		module.loadApplicationData();
		module.unload();
		verify( serviceRegistry ).generateTheMachineId();
		verify( serviceRegistry ).deregisterFromCluster( any( ApplicationData.class ) );
	}

	void provideDataNeededToExecuteTheModule() throws IOException {
		doReturn( "123" ).when( serviceRegistry ).generateTheMachineId();
		doReturn( "name" ).when( config ).getString( "server.smart-server.application.name" );
		doReturn( "1.0" ).when( config ).getString( "server.smart-server.application.version" );
		doReturn( true ).when( config ).getBoolean( "server.smart-server.enabled" );
		doReturn( serviceRegistry.getClass() ).when( config ).getClass( eq("server.smart-server.service-registry") );
	}

	@Test
	public void ensureWillNotStartModuleIfItIsNotEnabled() throws IOException {
		doReturn( false ).when( config ).getBoolean( "server.smart-server.enabled" );
		module.loadApplicationData();
		module.load( null, null );
		verify( serviceRegistry, never() ).generateTheMachineId();
		verify( serviceRegistry, never() ).registerIntoCluster( any( ApplicationData.class ) );
	}

	@Test( expected = InstantiationError.class )
	public void ensureWillFailIfNoServiceRegistryIsAvailable() throws IOException {
		doReturn( true ).when( config ).getBoolean( "server.smart-server.enabled" );
		module.loadApplicationData();
		module.load( null, null );
	}
}