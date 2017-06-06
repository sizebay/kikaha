package kikaha.urouting.serializers.jackson;

import java.io.IOException;
import javax.inject.Inject;
import io.undertow.security.idm.Credential;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import kikaha.core.modules.security.*;
import kikaha.urouting.api.Mimes;
import lombok.Data;

/**
 *
 */
public class JSONAuthenticationMechanism implements SimplifiedAuthenticationMechanism {

	@Inject Jackson jackson;
	@Inject DefaultAuthenticationConfiguration formAuthConfiguration;

	@Override
	public boolean sendAuthenticationChallenge( HttpServerExchange exchange, Session session ) {
		exchange.setStatusCode( StatusCodes.UNAUTHORIZED );
		exchange.getResponseSender().send( "UNAUTHORIZED" );
		exchange.endExchange();
		return true;
	}

	@Override
	public Credential readCredential(HttpServerExchange exchange) throws IOException {
		if ( Mimes.JSON.equals( exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE ) )
		&&   formAuthConfiguration.isTryingToLogin( exchange ) ) {
			final JSONCredentials json = jackson.objectMapper().readValue(exchange.getInputStream(), JSONCredentials.class);
			return new UsernameAndPasswordCredential(json.username, json.password);
		}
		return null;

	}

	@Data
	public static class JSONCredentials {
		String username;
		String password;
	}
}
