package kikaha.core.auth;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.impl.FormAuthenticationMechanism;
import kikaha.core.api.conf.AuthenticationRuleConfiguration;
import kikaha.core.api.conf.Configuration;
import lombok.val;
import trip.spi.Provided;

public class DefaultFormAuthenticationMechanism implements AuthenticationMechanismFactory {

	@Provided
	Configuration configuration;

	@Override
	public AuthenticationMechanism create( final AuthenticationRuleConfiguration rule ) {
		val config = configuration.authentication().formAuth();
		val postLocation = rule.pattern().replaceAll( "\\*$", "" ) + config.postLocation();
		return new FormAuthenticationMechanism(
			config.name(), config.loginPage(), config.errorPage(), postLocation );
	}
}
