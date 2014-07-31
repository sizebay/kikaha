package kikaha.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kikaha.core.api.AuthenticationConfiguration;
import kikaha.core.api.AuthenticationRuleConfiguration;
import lombok.Getter;
import lombok.val;
import lombok.experimental.Accessors;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

@Getter
@Accessors( fluent = true )
@SuppressWarnings( "rawtypes" )
public class DefaultAuthenticationConfiguration implements AuthenticationConfiguration {

	final Config config;

	final Map<String, Class<?>> mechanisms;
	final Map<String, Class<?>> identityManagers;
	final Map<String, Class<?>> notificationReceivers;
	final AuthenticationRuleConfiguration defaultRule;
	final List<AuthenticationRuleConfiguration> authenticationRules;

	public DefaultAuthenticationConfiguration( final Config config ) {
		this.config = config;
		mechanisms = retrieveChildElementsAsClassMapFromConfigNode( "mechanisms" );
		identityManagers = retrieveChildElementsAsClassMapFromConfigNode( "identity-managers" );
		notificationReceivers = retrieveChildElementsAsClassMapFromConfigNode( "notification-receivers" );
		defaultRule = new DefaultAuthenticationRule( config.getConfig( "default-rule" ) );
		authenticationRules = retrieveAuthenticationRules();
	}

	public Map<String, Class<?>> retrieveChildElementsAsClassMapFromConfigNode( String rootNode ) {
		val classList = new HashMap<String, Class<?>>();
		val node = config.getConfig( rootNode );
		for ( val entry : node.entrySet() )
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
		for ( val ruleConfig : config.getConfigList( "rules" ) )
			authRules.add( createAuthenticationRule( ruleConfig ) );
		return authRules;
	}

	private AuthenticationRuleConfiguration createAuthenticationRule( Config ruleConfig ) {
		return new InheritedAuthenticationRule( ruleConfig, defaultRule() );
	}
}
