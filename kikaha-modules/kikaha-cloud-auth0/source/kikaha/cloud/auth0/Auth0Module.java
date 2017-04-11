package kikaha.cloud.auth0;

import static kikaha.cloud.auth0.Auth0.TOKEN;

import javax.inject.*;
import java.io.IOException;
import com.auth0.Tokens;
import com.auth0.jwt.JWTVerifier;
import io.undertow.Undertow.Builder;
import io.undertow.server.*;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.core.modules.security.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Getter
@Singleton
public class Auth0Module implements Module {

	final String name = "post-security";

	@Inject JWTVerifier verifier;

	@Override
	public void load( Builder server, DeploymentContext context ) throws IOException {
		log.info( "Configuring Auth0..." );
		final HttpHandler rootHandler = context.rootHandler();
		final HttpHandler jwtHandler = new Auth0JWTTokenVerifierHandler( rootHandler );
		context.rootHandler( jwtHandler );
	}

	@RequiredArgsConstructor
	class Auth0JWTTokenVerifierHandler implements HttpHandler {

		final HttpHandler next;

		@Override
		public void handleRequest( HttpServerExchange httpServerExchange ) throws Exception {
			final SecurityContext securityContext = (SecurityContext)httpServerExchange.getSecurityContext();
			if ( securityContext != null ) {
				final Session currentSession = securityContext.getCurrentSession();
				final Tokens tokenString = (Tokens) currentSession.getAttribute( TOKEN );
				if ( tokenString != null )
					verifier.verify( tokenString.getIdToken() );
			}
			next.handleRequest( httpServerExchange );
		}
	}
}

