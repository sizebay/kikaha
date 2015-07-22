package kikaha.core.impl.conf;

import java.util.List;

import kikaha.core.api.conf.AuthenticationRuleConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException.Missing;

@RequiredArgsConstructor
class InheritedAuthenticationRuleConfiguration implements AuthenticationRuleConfiguration {

	final Config config;
	final AuthenticationRuleConfiguration inheritedRule;

	@Getter( lazy = true )
	private final String pattern = config.getString( "pattern" );

	@Getter( lazy = true )
	private final List<String> identityManagers = config.getStringList( "identity-manager" );

	@Getter( lazy = true )
	private final List<String> mechanisms = config.getStringList( "mechanisms" );

	@Getter( lazy = true )
	private final List<String> expectedRoles = config.getStringList( "expected-roles" );

	@Getter( lazy = true )
	private final List<String> exceptionPatterns = config.getStringList( "exclude-patterns" );

	@Override
	public String pattern() {
		try {
			return getPattern();
		} catch ( final Missing cause ) {
			return inheritedRule.pattern();
		}
	}

	@Override
	public List<String> mechanisms() {
		try {
			return getMechanisms();
		} catch ( final Missing cause ) {
			return inheritedRule.mechanisms();
		}
	}

	@Override
	public List<String> expectedRoles() {
		try {
			return getExpectedRoles();
		} catch ( final Missing cause ) {
			return inheritedRule.expectedRoles();
		}
	}

	@Override
	public List<String> identityManagers() {
		try {
			return getIdentityManagers();
		} catch ( final Missing cause ) {
			return inheritedRule.identityManagers();
		}
	}

	@Override
	public List<String> exceptionPatterns() {
		try {
			return getExceptionPatterns();
		} catch ( final Missing cause ) {
			return inheritedRule.exceptionPatterns();
		}
	}
}