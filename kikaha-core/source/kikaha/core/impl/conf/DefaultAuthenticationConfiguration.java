package kikaha.core.impl.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kikaha.core.api.conf.AuthenticationConfiguration;
import kikaha.core.api.conf.AuthenticationRuleConfiguration;
import kikaha.core.api.conf.FormAuthConfiguration;
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
	final Map<String, Class<?>> securityContextFactories;
	final AuthenticationRuleConfiguration defaultRule;
	final FormAuthConfiguration formAuth;
	final List<AuthenticationRuleConfiguration> authenticationRules;

	public DefaultAuthenticationConfiguration( final Config config ) {
		this.config = config;
		mechanisms = retrieveChildElementsAsClassMapFromConfigNode( "mechanisms" );
		identityManagers = retrieveChildElementsAsClassMapFromConfigNode( "identity-managers" );
		notificationReceivers = retrieveChildElementsAsClassMapFromConfigNode( "notification-receivers" );
		defaultRule = new DefaultAuthenticationRuleConfiguration( config.getConfig( "default-rule" ) );
		formAuth = new DefaultFormAuthConfiguration( config.getConfig( "form-auth" ) );
		authenticationRules = retrieveAuthenticationRules();
		securityContextFactories = retrieveChildElementsAsClassMapFromConfigNode( "security-context-factories" );
	}

	public Map<String, Class<?>> retrieveChildElementsAsClassMapFromConfigNode( final String rootNode ) {
		val classList = new HashMap<String, Class<?>>();
		val node = config.getConfig( rootNode );
		for ( val entry : node.entrySet() )
			classList.put( entry.getKey(), convertCanonicalNameToClass( entry ) );
		return classList;
	}

	Class convertCanonicalNameToClass( final Entry<String, ConfigValue> entry ) {
		final String classCanonicalName = entry.getValue().unwrapped().toString();
		return classFromCanonicalName( classCanonicalName );
	}

	public List<AuthenticationRuleConfiguration> retrieveAuthenticationRules() {
		val authRules = new ArrayList<AuthenticationRuleConfiguration>();
		for ( val ruleConfig : config.getConfigList( "rules" ) )
			authRules.add( createAuthenticationRule( ruleConfig ) );
		return authRules;
	}

	private AuthenticationRuleConfiguration createAuthenticationRule( final Config ruleConfig ) {
		return new InheritedAuthenticationRuleConfiguration( ruleConfig, defaultRule() );
	}

	Class classFromCanonicalName( final String classCanonicalName ) {
		try {
			return Class.forName( classCanonicalName );
		} catch ( final ClassNotFoundException cause ) {
			throw bypass( cause );
		}
	}

	IllegalStateException bypass( final Exception cause ) {
		return new IllegalStateException( cause );
	}
}