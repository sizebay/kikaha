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
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

@Getter
@Accessors( fluent = true )
//@SuppressWarnings( "rawtypes" )
public class AuthenticationRuleMatcher {

	final Map<String, AuthenticationMechanismFactory> mechanisms;
	final Map<String, IdentityManager> identityManagers;
	final Map<String, NotificationReceiver> notificationReceivers;
	final Map<String, SecurityContextFactory> securityContextFactories;
	final List<AuthenticationRule> rules;
	final AuthenticationConfiguration authConfig;
	final ServiceProvider provider;

	public AuthenticationRuleMatcher( final ServiceProvider provider, final AuthenticationConfiguration authConfig ) {
		this.authConfig = authConfig;
		this.provider = provider;
		mechanisms = instantiateMechanismsFoundOnConfig();
		identityManagers = instantiateIdentityManagersFoundOnConfig();
		notificationReceivers = instantiateNotificationReceivers();
		securityContextFactories = instantiateSecurityContextFactories();
		rules = readRulesFromConfig();
	}

	Map<String, AuthenticationMechanismFactory> instantiateMechanismsFoundOnConfig() {
		val mechanisms = new HashMap<String, AuthenticationMechanismFactory>();
		for ( val id : authConfig.mechanisms().keySet() ) {
			val originalClass = authConfig.mechanisms().get( id );
			mechanisms.put( id, (AuthenticationMechanismFactory)instantiate( originalClass ) );
		}
		return mechanisms;
	}

	Map<String, IdentityManager> instantiateIdentityManagersFoundOnConfig() {
		val identityManagers = new HashMap<String, IdentityManager>();
		for ( val id : authConfig.identityManagers().keySet() ) {
			val originalClass = authConfig.identityManagers().get( id );
			identityManagers.put( id, (IdentityManager)instantiate( originalClass ) );
		}
		return identityManagers;
	}

	Map<String, NotificationReceiver> instantiateNotificationReceivers() {
		val notificationReceivers = new HashMap<String, NotificationReceiver>();
		for ( val id : authConfig.notificationReceivers().keySet() ) {
			val originalClass = authConfig.notificationReceivers().get( id );
			notificationReceivers.put( id, (NotificationReceiver)instantiate( originalClass ) );
		}
		return notificationReceivers;
	}

	Map<String, SecurityContextFactory> instantiateSecurityContextFactories() {
		val securityContextFactories = new HashMap<String, SecurityContextFactory>();
		for ( val id : authConfig.securityContextFactories().keySet() ) {
			val originalClass = authConfig.securityContextFactories().get( id );
			securityContextFactories.put( id, (SecurityContextFactory)instantiate( originalClass ) );
		}
		return securityContextFactories;
	}

	<T> T instantiate( final Class<T> clazz ) {
		return provider.load(clazz);
	}

	List<AuthenticationRule> readRulesFromConfig() {
		val rules = new ArrayList<AuthenticationRule>();
		for ( val ruleConf : authConfig.authenticationRules() )
			rules.add( convertConfToRule( ruleConf ) );
		return rules;
	}

	AuthenticationRule convertConfToRule( final AuthenticationRuleConfiguration ruleConf ) {
		val identityManager = getIdentityManagerFor(ruleConf);
		val notificationReceiver = getNotificationReceiverFor(ruleConf);
		val securityContextFactory = getSecurityContextFor(ruleConf);
		val mechanisms = extractNeededMechanisms( ruleConf );
		return new AuthenticationRule(
				ruleConf.pattern(), identityManager,
				mechanisms, ruleConf.expectedRoles(),
			notificationReceiver, securityContextFactory,
			ruleConf.exceptionPatterns() );
	}

	List<IdentityManager> getIdentityManagerFor( final AuthenticationRuleConfiguration ruleConf ) {
		final List<IdentityManager> ims = new ArrayList<>();
		for ( final String name : ruleConf.identityManager() ){
			final IdentityManager identityManager = identityManagers().get( name );
			if ( identityManager == null )
				throw new IllegalArgumentException("No IdentityManager registered for " + name );
			ims.add( identityManager );
		}
		return ims;
	}

	NotificationReceiver getNotificationReceiverFor( final AuthenticationRuleConfiguration ruleConf) {
		return notificationReceivers().get( ruleConf.notificationReceiver() );
	}

	SecurityContextFactory getSecurityContextFor( final AuthenticationRuleConfiguration ruleConf) {
		val securityContextFactory = securityContextFactories().get( ruleConf.securityContextFactory() );
		if ( securityContextFactory == null )
			throw new IllegalArgumentException("No SecurityContextFactory registered for "
					+ ruleConf.securityContextFactory() );
		return securityContextFactory;
	}

	List<AuthenticationMechanism> extractNeededMechanisms(
			final AuthenticationRuleConfiguration ruleConf ) {
		val mechanisms = new ArrayList<AuthenticationMechanism>();
		for ( val mechanism : ruleConf.mechanisms() )
			mechanisms.add( createMechanism( ruleConf, mechanism ) );
		return mechanisms;
	}

	AuthenticationMechanism createMechanism( final AuthenticationRuleConfiguration ruleConf, final java.lang.String mechanism ) {
		try {
			val factory = mechanisms().get( mechanism );
			provider.provideOn( factory );
			return factory.create( ruleConf );
		} catch ( final ServiceProviderException e ) {
			throw new IllegalStateException( e );
		}
	}

	public AuthenticationRule retrieveAuthenticationRuleForUrl( final String url ) {
		if ( !isUrlFromAuthenticationResources( url ) )
			for ( val rule : rules )
				if ( rule.matches( url ) )
					return rule;
		return null;
	}

	boolean isUrlFromAuthenticationResources( final String url ) {
		val auth = authConfig.formAuth();
		return auth.errorPage().equals( url )
			|| auth.loginPage().equals( url )
			|| auth.permitionDeniedPage().equals( url );
	}
}
