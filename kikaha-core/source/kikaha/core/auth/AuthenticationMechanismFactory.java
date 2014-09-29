package kikaha.core.auth;

import io.undertow.security.api.AuthenticationMechanism;
import kikaha.core.api.conf.AuthenticationRuleConfiguration;

public interface AuthenticationMechanismFactory {

	AuthenticationMechanism create( final AuthenticationRuleConfiguration rule );
}
