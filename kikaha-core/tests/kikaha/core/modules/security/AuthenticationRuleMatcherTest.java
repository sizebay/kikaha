package kikaha.core.modules.security;

import static org.junit.Assert.*;
import javax.inject.Inject;
import kikaha.config.Config;
import kikaha.core.cdi.CDI;
import kikaha.core.test.KikahaRunner;
import lombok.val;
import org.junit.*;
import org.junit.runner.RunWith;
import tests.AssertThat;

@RunWith(KikahaRunner.class)
public class AuthenticationRuleMatcherTest {

	AuthenticationRuleMatcher matcher;

	@Inject CDI provider;
	@Inject Config config;
	@Inject
	AuthenticationEndpoints authenticationEndpoints;

	@Before
	public void initializeConfigurationAndMatcher() {
		matcher = new AuthenticationRuleMatcher( provider, config.getConfig( "server.auth" ), authenticationEndpoints);
	}

	@Test
	public void ensureThatCouldRetrieveRuleForProtectedURLAsDefinedInTestConfigurations() {
		val rule = matcher.retrieveAuthenticationRuleForUrl( "/users/" );
		assertNotNull( rule );
		AssertThat.isInstance( rule.mechanisms().get( 0 ), BasicAuthenticationMechanism.class );
		AssertThat.isInstance( rule.mechanisms().get( 1 ), BasicAuthenticationMechanism.class );
		AssertThat.isInstance( rule.identityManagers().get(0), FixedUserAndPasswordIdentityManager.class );
	}

	@Test
	public void ensureWillNotRetrieveRuleForExcludedURLAsDefinedInTestConfigurations() {
		val rule = matcher.retrieveAuthenticationRuleForUrl( "/ignore1/" );
		assertNull( rule );
		val secondRule = matcher.retrieveAuthenticationRuleForUrl( "/ignore2/" );
		assertNull( secondRule );
		val thirdRule = matcher.retrieveAuthenticationRuleForUrl( "/ignore1" );
		assertNotNull( thirdRule );
		val fourthRule = matcher.retrieveAuthenticationRuleForUrl( "/ignore3" );
		assertNull( fourthRule  );
	}

	@Test
	public void ensureThatCouldNotRetrieveRuleForUnprotectedURLAsDefinedInTestConfigurations() {
		val rule = matcher.retrieveAuthenticationRuleForUrl( "users/" );
		assertNull( rule );
	}
}
