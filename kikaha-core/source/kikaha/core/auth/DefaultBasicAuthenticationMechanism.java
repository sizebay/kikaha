package kikaha.core.auth;

import io.undertow.security.api.SecurityContext;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpServerExchange;

public class DefaultBasicAuthenticationMechanism extends BasicAuthenticationMechanism {

	public DefaultBasicAuthenticationMechanism() {
		super( "default" );
	}

	@Override
	public AuthenticationMechanismOutcome authenticate( HttpServerExchange exchange, SecurityContext securityContext ) {
		System.out.println( "BASIC.authenticate" );
		return super.authenticate( exchange, securityContext );
	}

	@Override
	public ChallengeResult sendChallenge( HttpServerExchange exchange, SecurityContext securityContext ) {
		System.out.println( "BASIC.sendChallenge" );
		return super.sendChallenge( exchange, securityContext );
	}
}
