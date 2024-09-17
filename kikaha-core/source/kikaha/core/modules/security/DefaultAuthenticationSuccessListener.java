package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;

/**
 *
 */
public class DefaultAuthenticationSuccessListener implements AuthenticationSuccessListener {

	@Override
	public void onAuthenticationSuccess( HttpServerExchange exchange, Session session, AuthenticationMechanism currentAuthMechanism ) {
		if ( currentAuthMechanism != null && !currentAuthMechanism.sendAuthenticationSuccess( exchange, session ) )
			throw new IllegalStateException( "Cannot send 'authentication success'" );
	}
}
