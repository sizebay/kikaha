package kikaha.core.auth;

import java.util.Map;

import kikaha.core.api.Configuration;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import lombok.val;
import lombok.extern.java.Log;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

@Log
@Singleton( exposedAs = DeploymentHook.class )
public class AuthenticationRulesDeployment implements DeploymentHook {
	
	@Provided
	ServiceProvider provider;
	
	@Provided
	Configuration configuration;

	@Override
	public void onDeploy( DeploymentContext context ) {
		if ( haveAuthenticationRulesDefinedInConfigurationFile() ) {
			log.info( "Configuring authentication rules..." );
			val ruleMatcher = createRuleMatcher();
			context.register( new AuthenticationHook( ruleMatcher ) );
		}
	}

	boolean haveAuthenticationRulesDefinedInConfigurationFile() {
		return configuration.authentication().authenticationRules().size() > 0;
	}

	AuthenticationRuleMatcher createRuleMatcher() {
		val ruleMatcher = new AuthenticationRuleMatcher( configuration.authentication() );
		provideOnMapEntries( ruleMatcher.identityManagers() );
		provideOnMapEntries( ruleMatcher.notificationReceivers() );
		provideOnMapEntries( ruleMatcher.mechanisms() );
		provideOnMapEntries( ruleMatcher.securityContextFactories() );
		return ruleMatcher;
	}
	
	<T> void provideOnMapEntries( Map<String, T> map ) {
		try {
			provider.provideOn( map.values() );
		} catch ( ServiceProviderException e ) {
			throw new IllegalStateException( e );
		}
	}

	@Override
	public void onUndeploy( DeploymentContext context ) {
	}
}