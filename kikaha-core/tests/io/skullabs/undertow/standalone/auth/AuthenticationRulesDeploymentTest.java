package io.skullabs.undertow.standalone.auth;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import io.skullabs.undertow.standalone.DefaultConfiguration;
import io.skullabs.undertow.standalone.api.Configuration;
import io.skullabs.undertow.standalone.api.DeploymentContext;

import java.util.Map;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class AuthenticationRulesDeploymentTest {

	@Mock
	DeploymentContext deploymentContext;
	AuthenticationRulesDeployment deployment;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
	}

	@Before
	public void initializeDeployment() throws ServiceProviderException {
		val deployment = new AuthenticationRulesDeployment();
		val provider = new ServiceProvider();
		provider.provideFor( Configuration.class, DefaultConfiguration.loadDefaultConfiguration() );
		provider.provideOn( deployment );
		this.deployment = spy( deployment );
	}

	@Test
	public void ensureThatHaveDeployedTheAuthenticationHook() {
		deployment.onDeploy( deploymentContext );
		verify( deploymentContext ).register( isA( AuthenticationHook.class ) );
	}

	@Test
	public void ensureThatWillNotDeployTheAuthenticationHookWhenNoAuthenticationRuleIsDefinedInConfigFile() {
		doReturn( false ).when( deployment ).haveAuthenticationRulesDefinedInConfigurationFile();
		deployment.onDeploy( deploymentContext );
		verify( deploymentContext, never() ).register( isA( AuthenticationHook.class ) );
	}

	@Test
	@SuppressWarnings( "unchecked" )
	public void ensureThatHaveProvideOnEveryMapReadFromConfig() {
		deployment.onDeploy( deploymentContext );
		verify( deployment, times( 3 ) ).provideOnMapEntries( any( Map.class ) );
	}
}
