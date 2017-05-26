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
public class JSONAuthenticationMechanism implements SimplifiedAuthenticationMechanism {

	@Inject Jackson jackson;

	@Override
	public boolean sendAuthenticationChallenge( HttpServerExchange exchange, Session session ) {
		exchange.setStatusCode( StatusCodes.UNAUTHORIZED );
		exchange.getResponseSender().send( "UNAUTHORIZED" );
		exchange.endExchange();
		return true;
	}

	@Override
	public Credential readCredential(HttpServerExchange exchange) throws IOException {
		final JSONCredentials json = jackson.objectMapper().readValue( exchange.getInputStream(), JSONCredentials.class );
		return new UsernameAndPasswordCredential( json.username, json.password );
	}

	@Data
	public static class JSONCredentials {
		String username;
		String password;
	}
}
