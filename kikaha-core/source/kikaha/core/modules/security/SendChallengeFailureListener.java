package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;

/**
 * An {@link AuthenticationFailureListener} implementation that sends an authentication challenge to the client.
 */
public class SendChallengeFailureListener implements AuthenticationFailureListener {

	@Override
	public void onAuthenticationFailure( HttpServerExchange exchange, Session session, AuthenticationMechanism currentAuthMechanism ) {
		if ( currentAuthMechanism != null && !currentAuthMechanism.sendAuthenticationChallenge( exchange, session ) )
			throw new IllegalStateException( "Cannot send authentication challenge" );
	}
}
