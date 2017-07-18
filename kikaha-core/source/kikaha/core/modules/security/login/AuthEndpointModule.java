package kikaha.core.modules.security.login;

import java.io.IOException;
import javax.inject.*;
import io.undertow.Undertow.Builder;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.core.modules.security.DefaultAuthenticationConfiguration;

/**
 * A module that easily deploy configurable endpoints for 'login' and 'logout' process.
 */
@Singleton
public class AuthEndpointModule implements Module {

	@Inject Config config;
	@Inject DefaultAuthenticationConfiguration formAuthConfiguration;
	@Inject AuthLoginHttpHandler loginHttpHandler;
	@Inject AuthLogoutHttpHandler logoutHttpHandler;
	@Inject AuthCallbackVerificationHttpHandler authCallbackVerificationHttpHandler;

	@Override
	public void load( Builder server, DeploymentContext context ) throws IOException {
		final boolean defaultEnabledState = config.getBoolean( "server.smart-routes.auth.enabled" );

		if ( !isEmpty( formAuthConfiguration.getLoginPage() )
		&&    config.getBoolean( "server.smart-routes.auth.login-form-enabled", defaultEnabledState ) )
			context.register( formAuthConfiguration.getLoginPage(), "GET", loginHttpHandler );

		if ( !isEmpty( formAuthConfiguration.getLogoutUrl() )
		&&   config.getBoolean( "server.smart-routes.auth.logout-url-enabled", defaultEnabledState ) )
		{
			final String method = config.getString( "server.smart-routes.auth.logout-http-method", "POST" );
			context.register( formAuthConfiguration.getLogoutUrl(), method, logoutHttpHandler );
		}

		if ( !isEmpty( formAuthConfiguration.getCallbackUrl() )
		&&   config.getBoolean( "server.smart-routes.auth.callback-url-enabled", defaultEnabledState )) {
			context.register( formAuthConfiguration.getCallbackUrl(), authCallbackVerificationHttpHandler );
		}
	}

	private boolean isEmpty(String logoutUrl) {
		return logoutUrl == null || logoutUrl.isEmpty();
	}
}
