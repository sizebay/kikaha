package kikaha.urouting.serializers.jackson;

import javax.inject.Inject;
import java.io.IOException;
import io.undertow.security.idm.*;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import kikaha.core.modules.security.*;
import kikaha.core.modules.security.IdentityManager;
import lombok.Data;

/**
 *
 */
public class JSONAuthenticationMechanism implements AuthenticationMechanism {

	@Inject Jackson jackson;

	@Override
	public Account authenticate( HttpServerExchange exchange, Iterable<IdentityManager> identityManagers, Session session ) {
		try {
			exchange.startBlocking();
			return authenticate( exchange, identityManagers );
		} catch ( IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	Account authenticate( HttpServerExchange exchange, Iterable<IdentityManager> identityManagers ) throws IOException {
		final JSONCredentials json = jackson.objectMapper().readValue( exchange.getInputStream(), JSONCredentials.class );
		final Credential credential = new UsernameAndPasswordCredential( json.username, json.password );
		return verify( identityManagers, credential );
	}

	@Override
	public boolean sendAuthenticationChallenge( HttpServerExchange exchange, Session session ) {
		exchange.setStatusCode( StatusCodes.UNAUTHORIZED );
		exchange.getResponseSender().send( "UNAUTHORIZED" );
		exchange.endExchange();
		return true;
	}

	@Data
	public static class JSONCredentials {
		String username;
		String password;
	}
}
