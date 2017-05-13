package kikaha.cloud.metrics;

import java.util.Arrays;
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
		module.healthChecks = Arrays.asList( healthCheck );
		Mockito.doReturn( "/sample" ).when( config ).getString( Matchers.eq( "server.health-check.url" ) );
	}

	@Test
	@SneakyThrows
	public void ensureCanDeployHealthChecks(){
		Mockito.doReturn( true ).when( config ).getBoolean( Matchers.eq( "server.health-check.enabled" ) );
		module.load( null, context );
		Mockito.verify( registry ).register( Matchers.anyString(), Matchers.eq( healthCheck ) );
		Mockito.verify( context ).register( Matchers.eq("/sample"), Matchers.eq("GET"), Matchers.any( HealthCheckHttpHandler.class ) );
	}

	@Test
	@SneakyThrows
	public void ensureWillNotDeployHealthChecksWhenTheModuleIsDisabled(){
		Mockito.doReturn( false ).when( config ).getBoolean( Matchers.eq( "server.health-check.enabled" ) );
		module.load( null, context );
		Mockito.verify( registry, Mockito.never() ).register( Matchers.anyString(), Matchers.any( HealthCheck.class ) );
		Mockito.verify( context, Mockito.never() ).register( Matchers.anyString(), Matchers.anyString(), Matchers.any( HttpHandler.class ) );
	}

	@Test( expected = UnsupportedOperationException.class )
	@SneakyThrows
	public void ensureWillFailToDeployHealthChecksWhenTheNoHealthChecksWasFound(){
		Mockito.doReturn( true ).when( config ).getBoolean( Matchers.eq( "server.health-check.enabled" ) );
		module.healthChecks = Arrays.asList();
		module.load( null, context );
	}
}