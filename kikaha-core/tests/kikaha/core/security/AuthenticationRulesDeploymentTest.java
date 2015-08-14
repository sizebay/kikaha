package kikaha.core.security;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import kikaha.core.security.AuthenticationHttpHandler;
import kikaha.core.security.AuthenticationRulesDeployment;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import trip.spi.DefaultServiceProvider;
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
		val provider = new DefaultServiceProvider();
		provider.providerFor( Configuration.class, DefaultConfiguration.loadDefaultConfiguration() );
		provider.provideOn( deployment );
		this.deployment = spy( deployment );
	}

	@Test
	public void ensureThatHaveDeployedTheAuthenticationHook() {
		deployment.onDeploy( deploymentContext );
		verify( deploymentContext ).rootHandler( isA( AuthenticationHttpHandler.class ) );
	}

	@Test
	public void ensureThatWillNotDeployTheAuthenticationHookWhenNoAuthenticationRuleIsDefinedInConfigFile() {
		doReturn( false ).when( deployment ).haveAuthenticationRulesDefinedInConfigurationFile();
		deployment.onDeploy( deploymentContext );
		verify( deploymentContext, never() ).rootHandler( isA( AuthenticationHttpHandler.class ) );
	}
}
