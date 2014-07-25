package io.skullabs.undertow.standalone;

import io.skullabs.undertow.standalone.api.AuthenticationConfiguration;
import io.skullabs.undertow.standalone.api.AuthenticationRule;

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
	private final Map<String, Class> mechisms = retrieveChildElementsAsClassMapFromConfigNode( "mechanisms" );

	@Getter( lazy = true )
	private final Class identityManagerClass = readIdentityManagerClass();

	@Getter( lazy = true )
	private final AuthenticationRule defaultRule = new DefaultAuthenticationRule( config.getConfig( "default-rule" ) );

	@Getter( lazy = true )
	private final List<AuthenticationRule> authenticationRules = retrieveAuthenticationRules();

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

	public Class readIdentityManagerClass() {
		return classFromCanonicalName( config.getString( "identity-manager" ) );
	}

	Class classFromCanonicalName( String classCanonicalName ) {
		try {
			return Class.forName( classCanonicalName );
		} catch ( ClassNotFoundException cause ) {
			throw new IllegalStateException( cause );
		}
	}

	public List<AuthenticationRule> retrieveAuthenticationRules() {
		val authRules = new ArrayList<AuthenticationRule>();
		for ( Config ruleConfig : config.getConfigList( "rules" ) )
			authRules.add( createAuthenticationRule( ruleConfig ) );
		return authRules;
	}

	private AuthenticationRule createAuthenticationRule( Config ruleConfig ) {
		return new InheritedAuthenticationRule( ruleConfig, defaultRule() );
	}
}
