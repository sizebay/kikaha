package kikaha.core.modules.security;

import kikaha.config.Config;
import kikaha.config.ConfigLoader;
import kikaha.core.cdi.DefaultServiceProvider;
import kikaha.core.cdi.ServiceProvider;
import kikaha.core.test.KikahaRunner;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import tests.AssertThat;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(KikahaRunner.class)
public class AuthenticationRuleMatcherTest {

	AuthenticationRuleMatcher matcher;

	@Inject
	ServiceProvider provider;

	@Inject
	Config config;

	@Before
	public void initializeConfigurationAndMatcher() {
		matcher = new AuthenticationRuleMatcher( provider, config.getConfig( "server.auth" ) );
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
	public void ensureThatCouldNotRetrieveRuleForUnprotectedURLAsDefinedInTestConfigurations() {
		val rule = matcher.retrieveAuthenticationRuleForUrl( "users/" );
		assertNull( rule );
	}
}
