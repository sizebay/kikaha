package kikaha.core.modules.security.login;

import javax.inject.Inject;
import io.undertow.server.*;
import kikaha.core.modules.security.*;
import kikaha.core.modules.undertow.BodyResponseSender;

/**
 *
 */
public class AuthCallbackVerificationHttpHandler implements HttpHandler {

	@Inject
	DefaultAuthenticationConfiguration authenticationConfiguration;

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		final SecurityContext securityContext = (SecurityContext)exchange.getSecurityContext();
		securityContext.authenticate();

		if ( !exchange.isResponseStarted() )
			BodyResponseSender
				.response( exchange,200, "plain/text","AUTHENTICATED" );
	}
}
