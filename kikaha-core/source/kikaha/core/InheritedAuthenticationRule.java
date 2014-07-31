package kikaha.core;

import java.util.List;

import kikaha.core.api.AuthenticationRuleConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException.Missing;

@RequiredArgsConstructor
class InheritedAuthenticationRule implements AuthenticationRuleConfiguration {

	final Config config;
	final AuthenticationRuleConfiguration inheritedRule;

	@Getter( lazy = true )
	private final String pattern = config.getString( "pattern" );

	@Getter( lazy = true )
	private final String identityManager = config.getString( "identity-manager" );

	@Getter( lazy = true )
	private final String notificationReceiver = config.getString( "notification-receiver" );

	@Getter( lazy = true )
	private final List<String> mechanisms = config.getStringList( "mechanisms" );

	@Getter( lazy = true )
	private final List<String> expectedRoles = config.getStringList( "expected-roles" );

	@Override
	public String pattern() {
		try {
			return getPattern();
		} catch ( Missing cause ) {
			return inheritedRule.pattern();
		}
	}

	@Override
	public List<String> mechanisms() {
		try {
			return getMechanisms();
		} catch ( Missing cause ) {
			return inheritedRule.mechanisms();
		}
	}

	@Override
	public List<String> expectedRoles() {
		try {
			return getExpectedRoles();
		} catch ( Missing cause ) {
			return inheritedRule.expectedRoles();
		}
	}

	@Override
	public String identityManager() {
		try {
			return getIdentityManager();
		} catch ( Missing cause ) {
			return inheritedRule.identityManager();
		}
	}

	@Override
	public String notificationReceiver() {
		try {
			return getNotificationReceiver();
		} catch ( Missing cause ) {
			return inheritedRule.notificationReceiver();
		}
	}
}