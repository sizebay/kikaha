package kikaha.urouting.it.context;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import kikaha.core.modules.security.*;

/**
 *
 */
public class FixedAuthenticationMechanism implements AuthenticationMechanism {

	public static final String USERNAME = "fixed-user";

	@Override
	public Account authenticate( HttpServerExchange exchange, Iterable<IdentityManager> identityManagers, Session session ) {
		final String id = exchange.getRequestHeaders().get( "id" ).getFirst();
		if ( "12".equals( id ) )
			return new FixedUsernameAndRolesAccount( USERNAME, "basic-user" );
		return null;
	}

	@Override
	public boolean sendAuthenticationChallenge( HttpServerExchange exchange, Session session ) {
		exchange.setStatusCode( StatusCodes.UNAUTHORIZED );
		return true;
	}
}
