package kikaha.core.modules.security;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import kikaha.core.DeploymentContext;
import kikaha.core.test.KikahaRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import java.util.List;

@RunWith(KikahaRunner.class)
public class AuthenticationRulesDeploymentTest {

	@Inject
	AuthenticationRulesDeployment deployment;

	@Mock
	DeploymentContext deploymentContext;

	@Mock
	AuthenticationRuleMatcher matcher;

	@Mock
	List<AuthenticationRule> rules;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		this.deployment = spy( deployment );
	}

	@Test
	public void ensureThatHaveDeployedTheAuthenticationHook() {
		deployment.load( null, deploymentContext );
		verify( deploymentContext ).rootHandler( isA( AuthenticationHttpHandler.class ) );
	}

	@Test
	public void ensureThatWillNotDeployTheAuthenticationHookWhenNoAuthenticationRuleIsDefinedInConfigFile() {
		doReturn( true ).when( rules ).isEmpty();
		doReturn( rules ).when( matcher ).rules();
		doReturn( matcher ).when( deployment ).createRuleMatcher();

		deployment.load( null, deploymentContext );
		verify( deploymentContext, never() ).rootHandler( isA( AuthenticationHttpHandler.class ) );
	}
}
