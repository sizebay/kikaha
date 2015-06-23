package kikaha.core.auth;

import java.util.Map;

import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentListener;
import kikaha.core.api.conf.Configuration;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

@Slf4j
@Singleton( exposedAs = DeploymentListener.class )
public class AuthenticationRulesDeployment implements DeploymentListener {

	@Provided
	ServiceProvider provider;

	@Provided
	Configuration configuration;

	@Override
	public void onDeploy( final DeploymentContext context ) {
		if ( haveAuthenticationRulesDefinedInConfigurationFile() ) {
			log.info( "Configuring authentication rules..." );
			val ruleMatcher = createRuleMatcher();
			val rootHandler = context.rootHandler();
			val authenticationHandler = new AuthenticationHttpHandler( ruleMatcher, configuration, rootHandler );
			context.rootHandler(authenticationHandler);
		}
	}

	boolean haveAuthenticationRulesDefinedInConfigurationFile() {
		return configuration.authentication().authenticationRules().size() > 0;
	}

	AuthenticationRuleMatcher createRuleMatcher() {
		val ruleMatcher = new AuthenticationRuleMatcher( provider, configuration.authentication() );
		provideOnMapEntries( ruleMatcher.identityManagers() );
		provideOnMapEntries( ruleMatcher.notificationReceivers() );
		provideOnMapEntries( ruleMatcher.mechanisms() );
		provideOnMapEntries( ruleMatcher.securityContextFactories() );
		return ruleMatcher;
	}

	<T> void provideOnMapEntries( final Map<String, T> map ) {
		try {
			provider.provideOn( map.values() );
		} catch ( final ServiceProviderException e ) {
			throw new IllegalStateException( e );
		}
	}

	@Override
	public void onUndeploy( final DeploymentContext context ) {
	}
}