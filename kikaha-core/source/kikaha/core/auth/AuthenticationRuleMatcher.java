package kikaha.core.auth;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.idm.IdentityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kikaha.core.api.conf.AuthenticationConfiguration;
import kikaha.core.api.conf.AuthenticationRuleConfiguration;
import lombok.Getter;
import lombok.val;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
@SuppressWarnings( "rawtypes" )
public class AuthenticationRuleMatcher {

	final Map<String, AuthenticationMechanism> mechanisms;
	final Map<String, IdentityManager> identityManagers;
	final Map<String, NotificationReceiver> notificationReceivers;
	final Map<String, SecurityContextFactory> securityContextFactories;
	final List<AuthenticationRule> rules;
	final AuthenticationConfiguration authConfig;

	public AuthenticationRuleMatcher( AuthenticationConfiguration authConfig ) {
		this.authConfig = authConfig;
		mechanisms = instantiateMechanismsFoundOnConfig();
		identityManagers = instantiateIdentityManagersFoundOnConfig();
		notificationReceivers = instantiateNotificationReceivers();
		securityContextFactories = instantiateSecurityContextFactories();
		rules = readRulesFromConfig();
	}

	Map<String, AuthenticationMechanism> instantiateMechanismsFoundOnConfig() {
		val mechanisms = new HashMap<String, AuthenticationMechanism>();
		for ( String id : authConfig.mechanisms().keySet() ) {
			val originalClass = authConfig.mechanisms().get( id );
			mechanisms.put( id, (AuthenticationMechanism)instantiate( originalClass ) );
		}
		return mechanisms;
	}

	Map<String, IdentityManager> instantiateIdentityManagersFoundOnConfig() {
		val identityManagers = new HashMap<String, IdentityManager>();
		for ( String id : authConfig.identityManagers().keySet() ) {
			val originalClass = authConfig.identityManagers().get( id );
			identityManagers.put( id, (IdentityManager)instantiate( originalClass ) );
		}
		return identityManagers;
	}

	Map<String, NotificationReceiver> instantiateNotificationReceivers() {
		val notificationReceivers = new HashMap<String, NotificationReceiver>();
		for ( String id : authConfig.notificationReceivers().keySet() ) {
			val originalClass = authConfig.notificationReceivers().get( id );
			notificationReceivers.put( id, (NotificationReceiver)instantiate( originalClass ) );
		}
		return notificationReceivers;
	}

	Map<String, SecurityContextFactory> instantiateSecurityContextFactories() {
		val securityContextFactories = new HashMap<String, SecurityContextFactory>();
		for ( String id : authConfig.securityContextFactories().keySet() ) {
			val originalClass = authConfig.securityContextFactories().get( id );
			securityContextFactories.put( id, (SecurityContextFactory)instantiate( originalClass ) );
		}
		return securityContextFactories;
	}

	Object instantiate( final Class clazz ) {
		try {
			return clazz.newInstance();
		} catch ( InstantiationException | IllegalAccessException e ) {
			throw new IllegalStateException( e );
		}
	}

	List<AuthenticationRule> readRulesFromConfig() {
		val rules = new ArrayList<AuthenticationRule>();
		for ( val ruleConf : authConfig.authenticationRules() )
			rules.add( convertConfToRule( ruleConf ) );
		return rules;
	}

	AuthenticationRule convertConfToRule( final AuthenticationRuleConfiguration ruleConf ) {
		val identityManager = identityManagers().get( ruleConf.identityManager() );
		val notificationReceiver = notificationReceivers().get( ruleConf.notificationReceiver() );
		val securityContextFactory = securityContextFactories().get( ruleConf.securityContextFactory() );
		val mechanisms = extractNeededMechanisms( ruleConf );
		return new AuthenticationRule(
				ruleConf.pattern(), identityManager,
				mechanisms, ruleConf.expectedRoles(),
				notificationReceiver, securityContextFactory );
	}

	List<AuthenticationMechanism> extractNeededMechanisms(
			final AuthenticationRuleConfiguration ruleConf ) {
		val mechanisms = new ArrayList<AuthenticationMechanism>();
		for ( val mechanism : ruleConf.mechanisms() )
			mechanisms.add( mechanisms().get( mechanism ) );
		return mechanisms;
	}

	public AuthenticationRule retrieveAuthenticationRuleForUrl( final String url ) {
		for ( val rule : rules )
			if ( rule.matches( url ) )
				return rule;
		return null;
	}
}
