package kikaha.core.modules.security;

import javax.inject.*;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.cdi.CDI;
import kikaha.core.modules.Module;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@Getter
public class AuthenticationModule implements Module {

	final String name = "security";

	@Inject CDI provider;
	@Inject Config config;
	@Inject FormAuthenticationConfiguration formAuthenticationConfiguration;
	@Inject SecurityConfiguration securityConfiguration;

	@Override
	public void load(Undertow.Builder builder, final DeploymentContext context ) {
		final AuthenticationRuleMatcher ruleMatcher = createRuleMatcher();
		if ( !ruleMatcher.rules().isEmpty() ) {
			log.info( "Configuring authentication rules..." );
			final HttpHandler rootHandler = context.rootHandler();
			final AuthenticationHttpHandler authenticationHandler = new AuthenticationHttpHandler(
					ruleMatcher, formAuthenticationConfiguration.getPermissionDeniedPage(),
					rootHandler, securityConfiguration );
			context.rootHandler(authenticationHandler);
		}
	}

	AuthenticationRuleMatcher createRuleMatcher() {
		return new AuthenticationRuleMatcher(
			provider, config.getConfig("server.auth"),
			formAuthenticationConfiguration );
	}
}