package kikaha.core.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import kikaha.core.impl.conf.DefaultAuthenticationConfiguration;
import kikaha.core.impl.conf.DefaultConfiguration;
import kikaha.core.security.AuthenticationRuleMatcher;
import kikaha.core.security.BasicAuthenticationMechanism;
import kikaha.core.security.FixedUserAndPasswordIdentityManager;
import lombok.val;

import org.junit.Before;
import org.junit.Test;

import tests.AssertThat;
import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProvider;

public class AuthenticationRuleMatcherTest {

	ServiceProvider provider = new DefaultServiceProvider();
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
		AssertThat.isInstance( rule.identityManagers().get(0), FixedUserAndPasswordIdentityManager.class );
	}

	@Test
	public void ensureThatCouldNotRetrieveRuleForUnprotectedURLAsDefinedInTestConfigurations() {
		val rule = matcher.retrieveAuthenticationRuleForUrl( "users/" );
		assertNull( rule );
	}

	@Test( expected=IllegalStateException.class )
	public void ensureThatThrowsExceptionWhenCantFindSecurityContextFactory(){
		final String path = "server.auth-invalid-security-context-factory";
		loadInvalidAuthConfigPath(path);
	}

	@Test( expected=IllegalArgumentException.class )
	public void ensureThatThrowsExceptionWhenCantFindIdentityManager(){
		final String path = "server.auth-invalid-identity-manager";
		loadInvalidAuthConfigPath(path);
	}

	private void loadInvalidAuthConfigPath(String path) {
		val loadedDefaultConfig = DefaultConfiguration.loadDefaultConfig();
		val invalidConfig = loadedDefaultConfig.getConfig( path ).withFallback(loadedDefaultConfig.getConfig("server.auth"));
		val authConfig = new DefaultAuthenticationConfiguration( invalidConfig );
		new AuthenticationRuleMatcher( provider, authConfig );
	}
}
