package kikaha.core.modules.security;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import java.util.List;
import javax.inject.Inject;
import io.undertow.server.HttpHandler;
import kikaha.core.DeploymentContext;
import kikaha.core.test.KikahaRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

@RunWith(KikahaRunner.class)
public class AuthenticationRulesDeploymentTest {

	@Inject
	AuthenticationModule deployment;

	@Mock
	DeploymentContext deploymentContext;

	@Mock
	AuthenticationRuleMatcher matcher;

	@Mock
	List<AuthenticationRule> rules;

	@Mock HttpHandler nextHandler;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		this.deployment = spy( deployment );
		doReturn( nextHandler ).when( deploymentContext ).rootHandler();
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
