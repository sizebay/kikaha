package kikaha.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import kikaha.core.auth.DefaultBasicAuthenticationMechanism;
import kikaha.core.auth.FixedUserAndPasswordIdentityManager;
import kikaha.core.impl.conf.DefaultAuthenticationConfiguration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.val;

import org.junit.Before;
import org.junit.Test;

public class DefaultAuthenticationConfigurationTest {

	DefaultAuthenticationConfiguration authConfig;

	@Before
	public void initializeAuthConfiguration() {
		val defaultConfig = DefaultConfiguration.loadDefaultConfig().getConfig( "server.auth" );
		this.authConfig = new DefaultAuthenticationConfiguration( defaultConfig );
	}

	@Test
	public void ensureThatCouldListMechanismsAsMapOfClasses() {
		val classMap = authConfig.retrieveChildElementsAsClassMapFromConfigNode( "mechanisms" );
		assertNotNull( classMap );
		assertEquals( classMap.get( "basic" ), DefaultBasicAuthenticationMechanism.class );
	}

	@Test
	public void ensureThatCouldListMechanismsAsMapOfClassesFromLazyGetter() {
		val classMap = authConfig.mechanisms();
		assertNotNull( classMap );
		assertEquals( classMap.get( "basic" ), DefaultBasicAuthenticationMechanism.class );
	}

	@Test
	public void ensureThatCouldReadIdentityManagerClass() {
		val classMap = authConfig.identityManagers();
		assertNotNull( classMap );
		assertEquals( classMap.get( "default" ), FixedUserAndPasswordIdentityManager.class );
	}

	@Test
	public void ensureThatCouldReadDefaultAuthenticationRule() {
		val defaultRule = authConfig.defaultRule();
		assertNotNull( defaultRule );
		assertThat( defaultRule.pattern(), is( "/*" ) );
		assertThat( defaultRule.expectedRoles().get( 0 ), is( "minimum-access-role" ) );
		assertThat( defaultRule.mechanisms().get( 0 ), is( "basic" ) );
		assertThat( defaultRule.mechanisms().get( 1 ), is( "form" ) );
		assertThat( defaultRule.identityManager(), is( "default" ) );
	}

	@Test
	public void ensureThatCouldReadAuthenticationRulesAndInheritsValuesFromDefaultRule() {
		val defaultRule = authConfig.defaultRule();
		assertNotNull( defaultRule );
		val inheritedRule = authConfig.authenticationRules().get( 0 );
		assertThat( inheritedRule.pattern(), is( defaultRule.pattern() ) );
		assertThat( inheritedRule.expectedRoles().get( 0 ), is( defaultRule.expectedRoles().get( 0 ) ) );
		assertThat( inheritedRule.mechanisms().get( 0 ), is( defaultRule.mechanisms().get( 0 ) ) );
		assertThat( inheritedRule.mechanisms().get( 1 ), is( "alternative" ) );
		assertThat( inheritedRule.securityContextFactory(), is( defaultRule.securityContextFactory() ) );
		assertThat( inheritedRule.securityContextFactory(), is( "default" ) );
	}
}
