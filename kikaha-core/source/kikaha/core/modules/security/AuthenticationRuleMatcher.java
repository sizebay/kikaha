package kikaha.core.modules.security;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import kikaha.config.Config;
import kikaha.core.cdi.CDI;
import kikaha.core.cdi.helpers.TinyList;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors( fluent = true )
public class AuthenticationRuleMatcher {

	final Config authConfig;
	final CDI provider;

	final SecurityContextFactory securityContextFactory;
	final Map<String, AuthenticationMechanism> mechanisms;
	final Map<String, IdentityManager> identityManagers;
	final List<AuthenticationRule> rules;
	final DefaultAuthenticationConfiguration defaultAuthenticationConfiguration;

	public AuthenticationRuleMatcher( final CDI provider, final Config authConfig, final DefaultAuthenticationConfiguration defaultAuthenticationConfiguration) {
		this.authConfig = authConfig;
		this.provider = provider;
		mechanisms = instantiateMechanismsFoundOnConfig();
		identityManagers = instantiateIdentityManagersFoundOnConfig();
		securityContextFactory = instantiateSecurityContextFactory( authConfig );
		rules = readRulesFromConfig();
		this.defaultAuthenticationConfiguration = defaultAuthenticationConfiguration;
	}

	private SecurityContextFactory instantiateSecurityContextFactory( final Config authConfig ) {
		final String className = authConfig.getString( "security-context-factory" );
		final SecurityContextFactory factory = instantiate( className, SecurityContextFactory.class );
		log.debug("Found SecurityContextFactory: " + factory);
		return factory;
	}

	private Map<String, AuthenticationMechanism> instantiateMechanismsFoundOnConfig() {
		final Map<String, Object> values = authConfig.getConfig("auth-mechanisms").toMap();
		final Map<String, AuthenticationMechanism> mechanisms = convert( values, o->instantiate( (String)o, AuthenticationMechanism.class ) );
		log.debug("Found Authentication Mechanisms: " + mechanisms);
		return mechanisms;
	}

	private Map<String, IdentityManager> instantiateIdentityManagersFoundOnConfig() {
		final Map<String, Object> values = authConfig.getConfig("identity-managers").toMap();
		final Map<String, IdentityManager> identityManagers = convert( values, o->instantiate( (String)o, IdentityManager.class ) );
		log.debug( "Found Identity Managers: " + identityManagers );
		return identityManagers;
	}

	private <V,N> Map<String,N> convert( Map<String, V> original, Function<V,N> converter  ) {
		final Map<String,N> newMap = new HashMap<>();
		for ( Map.Entry<String,V> entry : original.entrySet() ) {
			final N converted = converter.apply( entry.getValue() );
			newMap.put( entry.getKey(), converted );
		}
		return newMap;
	}

	@SuppressWarnings({"unused", "unchecked"})
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
		final List<String> defaultIdentityManagersAndAuthMechanisms = Collections.singletonList("default");
		final List<String> defaultExcludedPatterns = authConfig.getStringList("default-excluded-patterns");
		final List<IdentityManager> identityManagers = getIdentityManagerFor( ruleConf, defaultIdentityManagersAndAuthMechanisms );
		final List<AuthenticationMechanism> mechanisms = extractNeededMechanisms( ruleConf.getStringList("auth-mechanisms", defaultIdentityManagersAndAuthMechanisms) );
		final List<String> excludedPatterns = ruleConf.getStringList("exclude-patterns", new ArrayList<>());
		final boolean authenticationRequired = ruleConf.getBoolean( "authentication-required", true );
		excludedPatterns.addAll( defaultExcludedPatterns );
		return new AuthenticationRule(
				ruleConf.getString( "pattern" ), identityManagers,
				mechanisms, ruleConf.getStringList( "expected-roles", Collections.emptyList() ),
				excludedPatterns, authenticationRequired );
	}

	private List<IdentityManager> getIdentityManagerFor( Config ruleConf, List<String> defaultIdentityManagersAndAuthMechanisms ) {
		List<String> identityManagers = ruleConf.getStringList("identity-manager");
		if ( identityManagers != null && !identityManagers.isEmpty() ) {
			log.warn("The 'identity-manager' entry is deprecated.");
			log.warn("Consider use 'identity-managers'.");
		} else
			identityManagers = ruleConf.getStringList("identity-managers", defaultIdentityManagersAndAuthMechanisms );
		return getIdentityManagerFor( identityManagers );
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
		for ( final AuthenticationRule rule : rules )
			if ( rule.matches( url ) )
				return rule;
		return null;
	}
}
