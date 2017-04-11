package kikaha.core.modules.security.login;

import static org.mockito.Mockito.*;

import javax.inject.Inject;
import java.io.IOException;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.test.KikahaRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 * Unit tests for AuthEndpointModule.
 */
@RunWith( KikahaRunner.class )
public class AuthEndpointModuleTest {

	@Inject AuthEndpointModule module;
	@Mock Config config;
	@Mock DeploymentContext deploymentContext;

	@Before
	public void setupMocks(){
		MockitoAnnotations.initMocks( this );
		module.config = config;
	}

	@Test
	public void ensureCanDeployLoginAndLogoutByEnablingAuthSmartRoute() throws IOException {
		doReturn( true ).when( config ).getBoolean( eq("server.smart-routes.auth.enabled") );
		doReturn( true ).when( config ).getBoolean( eq("server.smart-routes.auth.login-form-enabled"), eq(true) );
		doReturn( true ).when( config ).getBoolean( eq("server.smart-routes.auth.logout-url-enabled"), eq(true) );
		doReturn( "GET" ).when( config ).getString( eq("server.smart-routes.auth.logout-http-method"), eq("POST") );

		module.load( null, deploymentContext );

		verify( deploymentContext ).register( eq("/auth/"), eq("GET"), eq(module.loginHttpHandler) );
		verify( deploymentContext ).register( eq("/auth/logout"), eq("GET"), eq(module.logoutHttpHandler) );
	}

	@Test
	public void ensureCanDeployOnlyLogin() throws IOException {
		doReturn( true ).when( config ).getBoolean( eq("server.smart-routes.auth.enabled") );
		doReturn( true ).when( config ).getBoolean( eq("server.smart-routes.auth.login-form-enabled"), eq(true) );
		doReturn( false ).when( config ).getBoolean( eq("server.smart-routes.auth.logout-url-enabled"), eq(true) );

		module.load( null, deploymentContext );

		verify( deploymentContext ).register( eq("/auth/"), eq("GET"), eq(module.loginHttpHandler) );
		verify( deploymentContext, never() ).register( eq("/auth/logout"), eq("POST"), eq(module.logoutHttpHandler) );
	}

	@Test
	public void ensureCanDeployOnlyLogout() throws IOException {
		doReturn( true ).when( config ).getBoolean( eq("server.smart-routes.auth.enabled") );
		doReturn( false ).when( config ).getBoolean( eq("server.smart-routes.auth.login-form-enabled"), eq(true) );
		doReturn( true ).when( config ).getBoolean( eq("server.smart-routes.auth.logout-url-enabled"), eq(true) );
		doReturn( "POST" ).when( config ).getString( eq("server.smart-routes.auth.logout-http-method"), eq("POST") );

		module.load( null, deploymentContext );

		verify( deploymentContext, never() ).register( eq("/auth/"), eq("GET"), eq(module.loginHttpHandler) );
		verify( deploymentContext ).register( eq("/auth/logout"), eq("POST"), eq(module.logoutHttpHandler) );
	}

	@Test
	public void ensureCanDisableAuthSmartRoute() throws IOException {
		doReturn( false ).when( config ).getBoolean( eq("server.smart-routes.auth.enabled") );
		doReturn( false ).when( config ).getBoolean( eq("server.smart-routes.auth.login-form-enabled"), eq(false) );
		doReturn( false ).when( config ).getBoolean( eq("server.smart-routes.auth.logout-url-enabled"), eq(false) );

		module.load( null, deploymentContext );

		verify( deploymentContext, never() ).register( eq("/auth/"), eq("GET"), eq(module.loginHttpHandler) );
		verify( deploymentContext, never() ).register( eq("/auth/logout"), eq("POST"), eq(module.logoutHttpHandler) );
	}
}