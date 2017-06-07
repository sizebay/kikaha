package kikaha.core.modules.security;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import java.util.*;
import javax.inject.Inject;
import io.undertow.server.HttpHandler;
import kikaha.core.DeploymentContext;
import kikaha.core.test.KikahaRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

@RunWith(KikahaRunner.class)
public class AuthenticationRulesDeploymentTest {

	@Inject SecurityModule deployment;

	@Mock DeploymentContext deploymentContext;
	@Mock AuthenticationRuleMatcher matcher;
	@Mock List<AuthenticationRule> rules;
	@Mock HttpHandler nextHandler;

	@Mock SecurityConfiguration securityConfiguration;
	@Mock DefaultAuthenticationConfiguration defaultAuthenticationConfiguration;

	@Mock AuthenticationRule ruleLowerPriority;
	@Mock AuthenticationMechanism mechanismLowerPriority;
	@Mock AuthenticationRule ruleHigherPriority;
	@Mock AuthenticationMechanism mechanismHigherPriority;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );

		deployment.securityConfiguration = securityConfiguration;
		deployment.defaultAuthenticationConfiguration = defaultAuthenticationConfiguration;
		this.deployment = spy( deployment );

		doReturn( nextHandler ).when( deploymentContext ).rootHandler();
		doReturn( 1 ).when( mechanismHigherPriority ).priority();
		doReturn( 0 ).when( mechanismLowerPriority ).priority();
		doReturn( asList(mechanismHigherPriority) ).when( ruleHigherPriority ).mechanisms();
		doReturn( asList(mechanismLowerPriority) ).when( ruleLowerPriority ).mechanisms();
		doReturn( "" ).when( defaultAuthenticationConfiguration ).getPermissionDeniedPage();
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

	@Test
	public void ensureWillExecuteConfigurationOnTheExpectedOrder(){
		doReturn( asList(ruleHigherPriority, ruleLowerPriority) ).when( matcher).rules();
		final Collection<AuthenticationMechanism> configurable = deployment.getConfigurableMechanisms(matcher);
		final Iterator<AuthenticationMechanism> iterator = configurable.iterator();
		assertEquals( mechanismHigherPriority, iterator.next() );
		assertEquals( mechanismLowerPriority, iterator.next() );
	}

	@Test
	public void ensureWillExecuteConfigurationOnTheExpectedOrder1(){
		doReturn( asList(ruleLowerPriority, ruleHigherPriority) ).when( matcher).rules();
		final Collection<AuthenticationMechanism> configurable = deployment.getConfigurableMechanisms(matcher);
		final Iterator<AuthenticationMechanism> iterator = configurable.iterator();
		assertEquals( mechanismHigherPriority, iterator.next() );
		assertEquals( mechanismLowerPriority, iterator.next() );
	}

	@Test
	public void ensureWillAskAuthenticationMechanismForConfiguration(){
		doReturn( asList(ruleHigherPriority, ruleLowerPriority) ).when( matcher ).rules();
		doReturn( matcher ).when( deployment ).createRuleMatcher();

		deployment.load( null, deploymentContext );

		verify( mechanismHigherPriority ).configure( eq(securityConfiguration), eq(defaultAuthenticationConfiguration) );
		verify( mechanismLowerPriority ).configure( eq(securityConfiguration), eq(defaultAuthenticationConfiguration) );
	}
}
