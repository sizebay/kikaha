package kikaha.core.modules.security;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.cdi.ServiceProvider;
import kikaha.core.modules.Module;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@Getter
public class AuthenticationModule implements Module {

	final String name = "security";

	@Inject
	ServiceProvider provider;

	@Inject
	Config config;

	SecurityContextFactory factory;

	@PostConstruct
	public void loadSecurityContextFactory(){
		final Class<?> clazz = config.getClass("server.auth.security-context-factory");
		factory = (SecurityContextFactory) provider.load(clazz);
	}

	@Override
	public void load(Undertow.Builder builder, final DeploymentContext context ) {
		final AuthenticationRuleMatcher ruleMatcher = createRuleMatcher();
		if ( !ruleMatcher.rules().isEmpty() ) {
			log.info( "Configuring authentication rules..." );
			final HttpHandler rootHandler = context.rootHandler();
			final String permissionDeniedPage = config.getString("server.auth.form-auth.permission-denied-page");
			final AuthenticationHttpHandler authenticationHandler = new AuthenticationHttpHandler( ruleMatcher, permissionDeniedPage, rootHandler, factory );
			context.rootHandler(authenticationHandler);
		}
	}

	AuthenticationRuleMatcher createRuleMatcher() {
		return new AuthenticationRuleMatcher( provider, config.getConfig("server.auth") );
	}
}