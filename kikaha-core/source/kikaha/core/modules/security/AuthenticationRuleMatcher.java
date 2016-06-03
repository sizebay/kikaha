package kikaha.core.modules.security;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import kikaha.config.Config;
import kikaha.core.cdi.ServiceProvider;
import kikaha.core.cdi.helpers.TinyList;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors( fluent = true )
public class AuthenticationRuleMatcher {

	final Config authConfig;
	final ServiceProvider provider;

	final SecurityContextFactory securityContextFactory;
	final Map<String, AuthenticationMechanism> mechanisms;
	final Map<String, IdentityManager> identityManagers;
	final List<AuthenticationRule> rules;

	final String loginPage;
	final String errorPage;
	final String permissionDeniedPage;

	public AuthenticationRuleMatcher( final ServiceProvider provider, final Config authConfig ) {
		this.authConfig = authConfig;
		this.provider = provider;
		mechanisms = instantiateMechanismsFoundOnConfig();
		identityManagers = instantiateIdentityManagersFoundOnConfig();
		securityContextFactory = instantiateSecurityContextFactory( authConfig );
		rules = readRulesFromConfig();
		loginPage = authConfig.getString( "form-auth.login-page" );
		errorPage = authConfig.getString( "form-auth.error-page" );
		permissionDeniedPage = authConfig.getString( "form-auth.permission-denied-page" );
	}

	private SecurityContextFactory instantiateSecurityContextFactory( final Config authConfig ) {
		final String className = authConfig.getString( "security-context-factory" );
		SecurityContextFactory factory = instantiate( className, SecurityContextFactory.class );
		log.debug("Found SecurityContextFactory: " + factory);
		return factory;
	}

	private Map<String, AuthenticationMechanism> instantiateMechanismsFoundOnConfig() {
		final Map<String, Object> values = authConfig.getConfig("auth-mechanisms").toMap();
		Map<String, AuthenticationMechanism> mechanisms = convert( values, o->instantiate( (String)o, AuthenticationMechanism.class ) );
		log.debug("Found Mechanisms: " + mechanisms);
		return mechanisms;
	}

	private Map<String, IdentityManager> instantiateIdentityManagersFoundOnConfig() {
		final Map<String, Object> values = authConfig.getConfig("identity-managers").toMap();
		Map<String, IdentityManager> identityManagers = convert( values, o->instantiate( (String)o, IdentityManager.class ) );
		log.debug( "Found Identity Managers: " + identityManagers );
		return identityManagers;
	}

	private <V,N> Map<String,N> convert( Map<String, V> original, Function<V,N> converter  ) {
		Map<String,N> newMap = new HashMap<>();
		for ( Map.Entry<String,V> entry : original.entrySet() ) {
			final N converted = converter.apply( entry.getValue() );
			newMap.put( entry.getKey(), converted );
		}
		return newMap;
	}

	private <T> T instantiate( String className, final Class<T> targetClazz ) {
		try {
			Class<T> clazz = (Class<T>) Class.forName( className );
			return provider.load( clazz );
		} catch ( Throwable cause ) {
			throw new IllegalStateException( "Can't load " + className, cause);
		}
	}

	private List<AuthenticationRule> readRulesFromConfig() {
		return authConfig.getConfigList("rules").stream()
				.map(this::convertConfToRule)
				.collect(Collectors.toList());
	}

	private AuthenticationRule convertConfToRule( final Config ruleConf ) {
		List<IdentityManager> identityManager = getIdentityManagerFor(ruleConf.getStringList("identity-manager"));
		List<AuthenticationMechanism> mechanisms = extractNeededMechanisms( ruleConf.getStringList("auth-mechanisms") );
		return new AuthenticationRule(
				ruleConf.getString( "pattern" ), identityManager,
				mechanisms, ruleConf.getStringList( "expected-roles" ),
			ruleConf.getStringList( "exclude-patterns" ) );
	}

	private List<IdentityManager> getIdentityManagerFor( final List<String> identityManagers ) {
		final List<IdentityManager> ims = new TinyList<>();
		for ( final String name : identityManagers ){
			final IdentityManager identityManager = identityManagers().get( name );
			if ( identityManager == null )
				throw new IllegalArgumentException("No IdentityManager registered for " + name );
			ims.add( identityManager );
		}
		return ims;
	}

	private List<AuthenticationMechanism> extractNeededMechanisms( final List<String> authMechanisms ) {
		return authMechanisms.stream()
				.map(mechanism -> mechanisms().get(mechanism))
				.collect(Collectors.toList());
	}

	public AuthenticationRule retrieveAuthenticationRuleForUrl( final String url ) {
		if ( !isUrlFromAuthenticationResources( url ) )
			for ( final AuthenticationRule rule : rules )
				if ( rule.matches( url ) )
					return rule;
		return null;
	}

	private boolean isUrlFromAuthenticationResources( final String url ) {
		return errorPage.equals( url )
			|| loginPage.equals( url )
			|| permissionDeniedPage.equals( url );
	}
}
