package io.skullabs.undertow.standalone;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import io.skullabs.undertow.standalone.auth.DefaultAdminOnlyIdentityManager;
import io.skullabs.undertow.standalone.auth.DefaultBasicAuthenticationMechanism;
import lombok.val;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings( "rawtypes" )
public class DefaultAuthenticationConfigurationTest {

	DefaultAuthenticationConfiguration authConfig;

	@Before
	public void initializeAuthConfiguration() {
		val defaultConfig = DefaultConfiguration.loadDefaultConfig().getConfig( "undertow.auth" );
		this.authConfig = new DefaultAuthenticationConfiguration( defaultConfig );
	}

	@Test
	public void ensureThatCouldListMechanismsAsMapOfClasses() {
		val classMap = authConfig.retrieveChildElementsAsClassMapFromConfigNode( "mechanisms" );
		assertNotNull( classMap );
		assertEquals( classMap.get( "default" ), DefaultBasicAuthenticationMechanism.class );
	}

	@Test
	public void ensureThatCouldListMechanismsAsMapOfClassesFromLazyGetter() {
		val classMap = authConfig.mechisms();
		assertNotNull( classMap );
		assertEquals( classMap.get( "default" ), DefaultBasicAuthenticationMechanism.class );
	}

	@Test
	public void ensureThatCouldReadIdentityManagerClass() {
		val identityManagerClass = authConfig.identityManagerClass();
		assertEquals( identityManagerClass, DefaultAdminOnlyIdentityManager.class );
	}

	@Test
	public void ensureThatCouldReadDefaultAuthenticationRule() {
		val defaultRule = authConfig.defaultRule();
		assertNotNull( defaultRule );
		assertThat( defaultRule.pattern(), is( "/*" ) );
		assertThat( defaultRule.expectedRoles().get( 0 ), is( "minimum-access-role" ) );
		assertThat( defaultRule.mechanisms().get( 0 ), is( "default" ) );
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
	}
}
