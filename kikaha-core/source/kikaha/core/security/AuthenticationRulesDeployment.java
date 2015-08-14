package kikaha.core.security;

import io.undertow.server.HttpHandler;

import javax.annotation.PostConstruct;

import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentListener;
import kikaha.core.api.conf.Configuration;
import lombok.extern.slf4j.Slf4j;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.Singleton;

@Slf4j
@Singleton( exposedAs = DeploymentListener.class )
public class AuthenticationRulesDeployment implements DeploymentListener {

	@Provided
	ServiceProvider provider;

	@Provided
	Configuration configuration;

	SecurityContextFactory factory;

	@PostConstruct
	public void loadSecurityContextFactory(){
		final Class<?> clazz = configuration.authentication().securityContextFactory();
		factory = (SecurityContextFactory) provider.load(clazz);
	}

	@Override
	public void onDeploy( final DeploymentContext context ) {
		if ( haveAuthenticationRulesDefinedInConfigurationFile() ) {
			log.info( "Configuring authentication rules..." );
			final AuthenticationRuleMatcher ruleMatcher = createRuleMatcher();
			final HttpHandler rootHandler = context.rootHandler();
			final AuthenticationHttpHandler authenticationHandler = new AuthenticationHttpHandler( ruleMatcher, configuration, rootHandler, factory );
			context.rootHandler(authenticationHandler);
		}
	}

	boolean haveAuthenticationRulesDefinedInConfigurationFile() {
		return configuration.authentication().authenticationRules().size() > 0;
	}

	AuthenticationRuleMatcher createRuleMatcher() {
		return new AuthenticationRuleMatcher( provider, configuration.authentication() );
	}

	@Override
	public void onUndeploy( final DeploymentContext context ) {
	}
}