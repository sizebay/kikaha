package io.skullabs.undertow.standalone;

import io.skullabs.undertow.standalone.api.AuthenticationRuleConfiguration;

import java.util.List;

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
	private final List<String> mechanisms = config.getStringList( "mechanisms" );

	@Getter( lazy = true )
	private final List<String> expectedRoles = config.getStringList( "expected-roles" );
}
