package kikaha.core;

import java.util.List;

import kikaha.core.api.AuthenticationRuleConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.typesafe.config.Config;

@Accessors( fluent = true )
@RequiredArgsConstructor
class DefaultAuthenticationRule implements AuthenticationRuleConfiguration {

	final Config config;

	@Getter( lazy = true )
	private final String pattern = config.getString( "pattern" );

	@Getter( lazy = true )
	private final String identityManager = config.getString( "identity-manager" );

	@Getter( lazy = true )
	private final String notificationReceiver = config.getString( "notification-receiver" );

	@Getter( lazy = true )
	private final String securityContextFactory = config.getString( "security-context-factory" );

	@Getter( lazy = true )
	private final List<String> mechanisms = config.getStringList( "mechanisms" );

	@Getter( lazy = true )
	private final List<String> expectedRoles = config.getStringList( "expected-roles" );
}
