package kikaha.core.auth;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import kikaha.core.impl.conf.DefaultAuthenticationConfiguration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.val;

import org.junit.Before;
import org.junit.Test;

import tests.AssertThat;
import trip.spi.ServiceProvider;

public class AuthenticationRuleMatcherTest {

	ServiceProvider provider = new ServiceProvider();
	AuthenticationRuleMatcher matcher;

	@Before
	public void initializeConfigurationAndMatcher() {
		val defaultConfig = DefaultConfiguration.loadDefaultConfig().getConfig( "server.auth" );
		val authConfig = new DefaultAuthenticationConfiguration( defaultConfig );
		this.matcher = new AuthenticationRuleMatcher( provider, authConfig );
	}

	@Test
	public void ensureThatCouldRetrieveRuleForProtectedURLAsDefinedInTestConfigurations() {
		val rule = matcher.retrieveAuthenticationRuleForUrl( "/users/" );
		assertNotNull( rule );
		AssertThat.isInstance( rule.mechanisms().get( 0 ), BasicAuthenticationMechanism.class );
		AssertThat.isInstance( rule.mechanisms().get( 1 ), BasicAuthenticationMechanism.class );
		AssertThat.isInstance( rule.identityManager(), FixedUserAndPasswordIdentityManager.class );
		assertNull( rule.notificationReceiver() );
		assertNotNull( rule.securityContextFactory() );
		AssertThat.isInstance( rule.securityContextFactory(), PrePopulatedSecurityContextFactory.class );
		AssertThat.isInstance( ( (PrePopulatedSecurityContextFactory)rule.securityContextFactory() ).wrapped,
			DefaultSecurityContextFactory.class );
	}

	@Test
	public void ensureThatCouldNotRetrieveRuleForUnprotectedURLAsDefinedInTestConfigurations() {
		val rule = matcher.retrieveAuthenticationRuleForUrl( "users/" );
		assertNull( rule );
	}
}
