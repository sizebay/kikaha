package io.skullabs.undertow.standalone;

import io.skullabs.undertow.standalone.api.AuthenticationConfiguration;
import io.skullabs.undertow.standalone.api.AuthenticationRuleConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.Accessors;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

@Accessors( fluent = true )
@RequiredArgsConstructor
@SuppressWarnings( "rawtypes" )
public class DefaultAuthenticationConfiguration implements AuthenticationConfiguration {

	final Config config;

	@Getter( lazy = true )
	private final Map<String, Class> mechanisms = retrieveChildElementsAsClassMapFromConfigNode( "mechanisms" );

	@Getter( lazy = true )
	private final Map<String, Class> identityManagers = retrieveChildElementsAsClassMapFromConfigNode( "identity-managers" );

	@Getter( lazy = true )
	private final AuthenticationRuleConfiguration defaultRule = new DefaultAuthenticationRule( config.getConfig( "default-rule" ) );

	@Getter( lazy = true )
	private final List<AuthenticationRuleConfiguration> authenticationRules = retrieveAuthenticationRules();

	public Map<String, Class> retrieveChildElementsAsClassMapFromConfigNode( String rootNode ) {
		val classList = new HashMap<String,Class>();
		val node = config.getConfig( rootNode );
		for ( Entry<String, ConfigValue> entry : node.entrySet() )
			classList.put( entry.getKey(), convertCanonicalNameToClass( entry ) );
		return classList;
	}

	Class convertCanonicalNameToClass( Entry<String, ConfigValue> entry ) {
		String classCanonicalName = entry.getValue().unwrapped().toString();
		return classFromCanonicalName( classCanonicalName );
	}

	Class classFromCanonicalName( String classCanonicalName ) {
		try {
			return Class.forName( classCanonicalName );
		} catch ( ClassNotFoundException cause ) {
			throw new IllegalStateException( cause );
		}
	}

	public List<AuthenticationRuleConfiguration> retrieveAuthenticationRules() {
		val authRules = new ArrayList<AuthenticationRuleConfiguration>();
		for ( Config ruleConfig : config.getConfigList( "rules" ) )
			authRules.add( createAuthenticationRule( ruleConfig ) );
		return authRules;
	}

	private AuthenticationRuleConfiguration createAuthenticationRule( Config ruleConfig ) {
		return new InheritedAuthenticationRule( ruleConfig, defaultRule() );
	}
}
