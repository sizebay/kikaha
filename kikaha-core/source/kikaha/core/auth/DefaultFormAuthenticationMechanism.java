package kikaha.core.auth;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.impl.FormAuthenticationMechanism;
import io.undertow.server.HttpServerExchange;
import kikaha.core.api.conf.Configuration;
import lombok.Getter;
import lombok.val;
import trip.spi.Provided;

public class DefaultFormAuthenticationMechanism implements AuthenticationMechanism {

	@Provided
	Configuration configuration;

	@Getter( lazy = true )
	private final AuthenticationMechanism mechanism = createFormAuthMechanism();

	AuthenticationMechanism createFormAuthMechanism() {
		val config = configuration.authentication().formAuth();
		return new FormAuthenticationMechanism(
			config.name(),
			config.loginPage(),
			config.errorPage(),
			config.postLocation() );
	}

	@Override
	public AuthenticationMechanismOutcome authenticate(
		final HttpServerExchange exchange, final SecurityContext securityContext ) {
		return getMechanism().authenticate( exchange, securityContext );
	}

	@Override
	public ChallengeResult sendChallenge(
		final HttpServerExchange exchange, final SecurityContext securityContext ) {
		return getMechanism().sendChallenge( exchange, securityContext );
	}
}
