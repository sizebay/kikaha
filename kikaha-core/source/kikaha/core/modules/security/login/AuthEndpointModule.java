package kikaha.core.modules.security.login;

import javax.inject.*;
import java.io.IOException;
import io.undertow.Undertow.Builder;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.core.modules.security.FormAuthenticationConfiguration;

/**
 * A module that easily deploy configurable endpoints for 'login' and 'logout' process.
 */
@Singleton
public class AuthEndpointModule implements Module {

	@Inject Config config;
	@Inject FormAuthenticationConfiguration formAuthConfiguration;
	@Inject AuthLoginHttpHandler loginHttpHandler;
	@Inject AuthLogoutHttpHandler logoutHttpHandler;

	@Override
	public void load( Builder server, DeploymentContext context ) throws IOException {
		final boolean defaultEnabledState = config.getBoolean( "server.smart-routes.auth.enabled" );
		if ( config.getBoolean( "server.smart-routes.auth.login-form-enabled", defaultEnabledState ) )
			context.register( formAuthConfiguration.getLoginPage(), "GET", loginHttpHandler );
		if ( config.getBoolean( "server.smart-routes.auth.logout-url-enabled", defaultEnabledState ) )
			context.register( formAuthConfiguration.getLogoutUrl(), "POST", logoutHttpHandler );
	}
}
