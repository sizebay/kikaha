package kikaha.cloud.smart;

import static org.mockito.Mockito.*;
import java.io.IOException;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.config.Config;
import kikaha.core.cdi.CDI;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * Unit tests for {@link SmartServerModule}.
 */
@RunWith( MockitoJUnitRunner.class )
public class SmartServerModuleTest {

	@Mock Config config;
	@Mock ServiceRegistry serviceRegistry;
	@Mock CDI cdi;
	@Mock
    MachineIdentification machineIdentification;

	@InjectMocks
	@Spy SmartServerModule module;

	@Before
	public void configureMocks() throws IOException {
		doReturn( machineIdentification.getClass() ).when( config ).getClass( eq("server.smart-server.local-address.identification") );
		doReturn(machineIdentification).when( cdi ).load( eq( machineIdentification.getClass()) );
		doReturn( "10.0.0.1" ).when(machineIdentification).getLocalAddress();
		doReturn( serviceRegistry ).when( cdi ).load( eq(serviceRegistry.getClass()) );
		doReturn( true ).when( config ).getBoolean( eq("server.http.enabled") );
		doReturn( 9000 ).when( config ).getInteger( eq("server.http.port") );
		doAnswer(this::registerIntoCluster).when( serviceRegistry ).registerIntoCluster( any() );
	}

	@Test
	public void ensureCanExecuteTheServiceRegistry() throws IOException {
		provideDataNeededToExecuteTheModule();
		module.loadApplicationData();
		module.load( null, null );
		verify(machineIdentification).generateTheMachineId();
		verify(machineIdentification).getLocalAddress();
		verify( serviceRegistry ).registerIntoCluster( any( ApplicationData.class ) );
	}

	@Test
	public void ensureCanDeregisterTheService() throws IOException {
		provideDataNeededToExecuteTheModule();
		module.loadApplicationData();
		module.unload();
		verify( serviceRegistry ).deregisterFromCluster( any( ApplicationData.class ) );
	}

	void provideDataNeededToExecuteTheModule() throws IOException {
		doReturn( "123" ).when(machineIdentification).generateTheMachineId();
		doReturn( "127.0.0.1" ).when(machineIdentification).getLocalAddress();
		doReturn( "name" ).when( config ).getString( "server.smart-server.application.name" );
		doReturn( "1.0" ).when( config ).getString( "server.smart-server.application.version" );
		doReturn( true ).when( config ).getBoolean( "server.smart-server.enabled" );
		doReturn( machineIdentification.getClass() ).when( config ).getClass( "server.smart-server.local-address.identification" );
		doReturn( serviceRegistry.getClass() ).when( config ).getClass( eq("server.smart-server.service-registry") );
	}

	Answer registerIntoCluster(InvocationOnMock i) {
		final ApplicationData applicationData = i.getArgumentAt(0, ApplicationData.class);
		applicationData.getMachineId();
		applicationData.getLocalAddress();
		return null;
	}
}