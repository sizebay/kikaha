package kikaha.core.auth;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import kikaha.core.api.conf.AuthenticationRuleConfiguration;

public class DefaultBasicAuthenticationMechanism implements AuthenticationMechanismFactory {

	@Override
	public AuthenticationMechanism create( final AuthenticationRuleConfiguration rule ) {
		return new BasicAuthenticationMechanism( "default" );
	}
}
