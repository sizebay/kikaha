package io.skullabs.undertow.standalone.auth;

import io.skullabs.undertow.standalone.api.Configuration;
import io.skullabs.undertow.standalone.api.DeploymentContext;
import io.skullabs.undertow.standalone.api.DeploymentHook;
import io.skullabs.undertow.standalone.api.RequestHook;
import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
import io.undertow.server.HttpServerExchange;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.java.Log;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

@Log
@Singleton
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
			val authHook = new AuthenticationHook( ruleMatcher );
			context.register( new IOThreadAuthenticationHook( authHook ) );
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

@RequiredArgsConstructor
class IOThreadAuthenticationHook implements RequestHook {

	final AuthenticationHook hook;

	@Override
	public void execute( RequestHookChain chain, HttpServerExchange exchange ) throws UndertowStandaloneException {
		chain.executeInIOThread( hook );
	}
}