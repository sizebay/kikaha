package kikaha.core.impl.conf;

import java.util.List;

import kikaha.core.api.conf.AuthenticationRuleConfiguration;
import lombok.Getter;
import lombok.experimental.Accessors;

import com.typesafe.config.Config;

@Getter
@Accessors( fluent = true )
class DefaultAuthenticationRuleConfiguration implements AuthenticationRuleConfiguration {

	final String pattern;
	final String identityManager;
	final String notificationReceiver;
	final String securityContextFactory;
	final List<String> mechanisms;
	final List<String> expectedRoles;
	final List<String> exceptionPatterns;
	final Config config;

	public DefaultAuthenticationRuleConfiguration( final Config config ) {
		this.pattern = config.getString( "pattern" );
		this.identityManager = config.getString( "identity-manager" );
		this.notificationReceiver = config.getString( "notification-receiver" );
		this.securityContextFactory = config.getString( "security-context-factory" );
		this.mechanisms = config.getStringList( "mechanisms" );
		this.expectedRoles = config.getStringList( "expected-roles" );
		this.exceptionPatterns = config.getStringList( "exclude-patterns" );
		this.config = config;
	}
}
