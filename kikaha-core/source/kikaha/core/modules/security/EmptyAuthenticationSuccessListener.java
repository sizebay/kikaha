package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;

/**
 *
 */
public class EmptyAuthenticationSuccessListener implements AuthenticationSuccessListener {

	@Override
	public void onAuthenticationSuccess( HttpServerExchange exchange, Session session, AuthenticationMechanism currentAuthMechanism ) {

	}
}
