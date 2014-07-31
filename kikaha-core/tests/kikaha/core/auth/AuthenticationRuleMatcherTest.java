package kikaha.core.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import kikaha.core.DefaultAuthenticationConfiguration;
import kikaha.core.DefaultConfiguration;
import kikaha.core.auth.AuthenticationRuleMatcher;
import kikaha.core.auth.DefaultAdminOnlyIdentityManager;
import kikaha.core.auth.DefaultBasicAuthenticationMechanism;
import lombok.val;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationRuleMatcherTest {

	AuthenticationRuleMatcher matcher;

	@Before
	public void initializeConfigurationAndMatcher() {
		val defaultConfig = DefaultConfiguration.loadDefaultConfig().getConfig( "undertow.auth" );
		val authConfig = new DefaultAuthenticationConfiguration( defaultConfig );
		this.matcher = new AuthenticationRuleMatcher( authConfig );
	}

	@Test
	public void ensureThatCouldRetrieveRuleForProtectedURLAsDefinedInTestConfigurations() {
		val rule = matcher.retrieveAuthenticationRuleForUrl( "/users/" );
		assertNotNull( rule );
		assertThat( rule.mechanisms().get( 0 ), is( DefaultBasicAuthenticationMechanism.class ) );
		assertThat( rule.mechanisms().get( 1 ), is( DefaultBasicAuthenticationMechanism.class ) );
		assertThat( rule.identityManager(), is( DefaultAdminOnlyIdentityManager.class ) );
		assertNull( rule.notificationReceiver() );
	}

	@Test
	public void ensureThatCouldNotRetrieveRuleForUnprotectedURLAsDefinedInTestConfigurations() {
		val rule = matcher.retrieveAuthenticationRuleForUrl( "users/" );
		assertNull( rule );
	}
}
