package io.skullabs.undertow.standalone.auth;

import io.skullabs.undertow.standalone.api.Configuration;
import io.skullabs.undertow.standalone.api.DeploymentContext;
import io.skullabs.undertow.standalone.api.DeploymentHook;

import java.util.Map;

import lombok.val;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

@Singleton
public class AuthenticationRulesDeployment implements DeploymentHook {
	
	@Provided
	ServiceProvider provider;
	
	@Provided
	Configuration configuration;

	@Override
	public void onDeploy( DeploymentContext context ) {
		if ( haveAuthenticationRulesDefinedInConfigurationFile() ) {
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
