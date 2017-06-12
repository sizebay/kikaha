package kikaha.urouting.serializers.jackson;

import io.undertow.security.idm.Credential;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import kikaha.core.util.Tuple;
import kikaha.core.modules.security.DefaultAuthenticationConfiguration;
import kikaha.core.modules.security.Session;
import kikaha.core.modules.security.SimplifiedAuthenticationMechanism;
import kikaha.core.modules.security.UsernameAndPasswordCredential;
import kikaha.core.url.URL;
import kikaha.urouting.api.Mimes;
import lombok.Data;

import javax.inject.Inject;
import java.io.IOException;

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
	public boolean sendAuthenticationSuccess(HttpServerExchange exchange, Session session) {
		exchange.setStatusCode( StatusCodes.OK );
		exchange.getResponseSender().send( "AUTHORIZED" );
		exchange.endExchange();
		return true;
	}

	@Override
	public Credential readCredential(HttpServerExchange exchange) throws IOException {
		final Tuple<String,String> contentTypeAndEncoding = URL
			.fixContentType( exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE ), null );

		if ( Mimes.JSON.equals( contentTypeAndEncoding.getFirst() ) &&   formAuthConfiguration.isTryingToLogin( exchange ) ) {
			if ( !exchange.isBlocking() )
				exchange.startBlocking();

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
