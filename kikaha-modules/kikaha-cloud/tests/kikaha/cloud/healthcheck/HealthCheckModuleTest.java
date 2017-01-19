package kikaha.cloud.healthcheck;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import com.codahale.metrics.health.*;
import io.undertow.server.HttpHandler;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.test.KikahaRunner;
import lombok.SneakyThrows;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 * Unit tests for {@link HealthCheckModule}.
 */
@RunWith( KikahaRunner.class )
public class HealthCheckModuleTest {

	@Mock Config config;
	@Mock HealthCheckRegistry registry;
	@Mock HealthCheck healthCheck;
	@Mock DeploymentContext context;

	@InjectMocks
	@Spy HealthCheckModule module;

	@Before
	public void configureMocks() {
		MockitoAnnotations.initMocks( this );
		module.healthChecks = asList( healthCheck );
		doReturn( "/sample" ).when( config ).getString( eq( "server.health-check.url" ) );
	}

	@Test
	@SneakyThrows
	public void ensureCanDeployHealthChecks(){
		doReturn( true ).when( config ).getBoolean( eq( "server.health-check.enabled" ) );
		module.load( null, context );
		verify( registry ).register( anyString(), eq( healthCheck ) );
		verify( context ).register( anyString(), anyString(), any( HealthCheckHttpHandler.class ) );
	}

	@Test
	@SneakyThrows
	public void ensureWillNotDeployHealthChecksWhenTheModuleIsDisabled(){
		doReturn( false ).when( config ).getBoolean( eq( "server.health-check.enabled" ) );
		module.load( null, context );
		verify( registry, never() ).register( anyString(), any( HealthCheck.class ) );
		verify( context, never() ).register( anyString(), anyString(), any( HttpHandler.class ) );
	}

	@Test( expected = UnsupportedOperationException.class )
	@SneakyThrows
	public void ensureWillFailToDeployHealthChecksWhenTheNoHealthChecksWasFound(){
		doReturn( true ).when( config ).getBoolean( eq( "server.health-check.enabled" ) );
		module.healthChecks = asList();
		module.load( null, context );
	}
}