package kikaha.core.modules.security.login;

import java.io.IOException;
import javax.inject.*;
import io.undertow.Undertow.Builder;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.core.modules.security.AuthenticationEndpoints;

/**
 * A module that easily deploy configurable endpoints for 'login' and 'logout' process.
 */
@Singleton
public class AuthEndpointModule implements Module {

	@Inject Config config;
	@Inject AuthenticationEndpoints authenticationEndpoints;
	@Inject AuthLoginHttpHandler loginHttpHandler;
	@Inject AuthLogoutHttpHandler logoutHttpHandler;
	@Inject AuthCallbackVerificationHttpHandler authCallbackVerificationHttpHandler;

	@Override
	public void load( Builder server, DeploymentContext context ) throws IOException {
		final boolean defaultEnabledState = config.getBoolean( "server.smart-routes.auth.enabled" );

		if ( !isEmpty( authenticationEndpoints.getLoginPage() )
		&&    config.getBoolean( "server.smart-routes.auth.login-form-enabled", defaultEnabledState ) )
			context.register( authenticationEndpoints.getLoginPage(), "GET", loginHttpHandler );

		if ( !isEmpty( authenticationEndpoints.getLogoutUrl() )
		&&   config.getBoolean( "server.smart-routes.auth.logout-url-enabled", defaultEnabledState ) )
		{
			context.register( authenticationEndpoints.getLogoutUrl(),
				authenticationEndpoints.getLogoutUrlMethod(), logoutHttpHandler );
		}

		if ( !isEmpty( authenticationEndpoints.getCallbackUrl() )
		&&   config.getBoolean( "server.smart-routes.auth.callback-url-enabled", defaultEnabledState )) {
			context.register(
				authenticationEndpoints.getCallbackUrl(),
				authenticationEndpoints.getCallbackUrlMethod(),
				authCallbackVerificationHttpHandler );
		}
	}

	private boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}
}
