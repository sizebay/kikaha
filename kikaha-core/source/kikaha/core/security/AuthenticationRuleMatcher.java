package kikaha.core.security;

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

@Getter
@Accessors( fluent = true )
public class AuthenticationRuleMatcher {

	final Map<String, AuthenticationMechanism> mechanisms;
	final Map<String, IdentityManager> identityManagers;
	final SecurityContextFactory securityContextFactory;
	final List<AuthenticationRule> rules;
	final AuthenticationConfiguration authConfig;
	final ServiceProvider provider;

	public AuthenticationRuleMatcher( final ServiceProvider provider, final AuthenticationConfiguration authConfig ) {
		this.authConfig = authConfig;
		this.provider = provider;
		mechanisms = instantiateMechanismsFoundOnConfig();
		identityManagers = instantiateIdentityManagersFoundOnConfig();
		securityContextFactory = instantiateSecurityContextFactory( authConfig );
		rules = readRulesFromConfig();
	}

	private SecurityContextFactory instantiateSecurityContextFactory( final AuthenticationConfiguration authConfig ) {
		return (SecurityContextFactory)instantiate( authConfig.securityContextFactory() );
	}

	private Map<String, AuthenticationMechanism> instantiateMechanismsFoundOnConfig() {
		val mechanisms = new HashMap<String, AuthenticationMechanism>();
		for ( val id : authConfig.mechanisms().keySet() ) {
			val originalClass = authConfig.mechanisms().get( id );
			mechanisms.put( id, (AuthenticationMechanism)instantiate( originalClass ) );
		}
		return mechanisms;
	}

	private Map<String, IdentityManager> instantiateIdentityManagersFoundOnConfig() {
		val identityManagers = new HashMap<String, IdentityManager>();
		for ( val id : authConfig.identityManagers().keySet() ) {
			val originalClass = authConfig.identityManagers().get( id );
			identityManagers.put( id, (IdentityManager)instantiate( originalClass ) );
		}
		return identityManagers;
	}

	private <T> T instantiate( final Class<T> clazz ) {
		try {
			return provider.load( clazz );
		} catch ( Throwable cause ) {
			System.out.println( "Can't load " + clazz );
			throw cause;
		}
	}

	private List<AuthenticationRule> readRulesFromConfig() {
		val rules = new ArrayList<AuthenticationRule>();
		for ( val ruleConf : authConfig.authenticationRules() )
			rules.add( convertConfToRule( ruleConf ) );
		return rules;
	}

	private AuthenticationRule convertConfToRule( final AuthenticationRuleConfiguration ruleConf ) {
		val identityManager = getIdentityManagerFor(ruleConf);
		val mechanisms = extractNeededMechanisms( ruleConf );
		return new AuthenticationRule(
				ruleConf.pattern(), identityManager,
				mechanisms, ruleConf.expectedRoles(),
			ruleConf.exceptionPatterns() );
	}

	private List<IdentityManager> getIdentityManagerFor( final AuthenticationRuleConfiguration ruleConf ) {
		final List<IdentityManager> ims = new ArrayList<>();
		for ( final String name : ruleConf.identityManagers() ){
			final IdentityManager identityManager = identityManagers().get( name );
			if ( identityManager == null )
				throw new IllegalArgumentException("No IdentityManager registered for " + name );
			ims.add( identityManager );
		}
		return ims;
	}

	private List<AuthenticationMechanism> extractNeededMechanisms(
			final AuthenticationRuleConfiguration ruleConf ) {
		val mechanisms = new ArrayList<AuthenticationMechanism>();
		for ( val mechanism : ruleConf.mechanisms() )
			mechanisms.add( mechanisms().get( mechanism ) );
		return mechanisms;
	}

	public AuthenticationRule retrieveAuthenticationRuleForUrl( final String url ) {
		if ( !isUrlFromAuthenticationResources( url ) )
			for ( val rule : rules )
				if ( rule.matches( url ) )
					return rule;
		return null;
	}

	private boolean isUrlFromAuthenticationResources( final String url ) {
		val auth = authConfig.formAuth();
		return auth.errorPage().equals( url )
			|| auth.loginPage().equals( url )
			|| auth.permitionDeniedPage().equals( url );
	}
}
