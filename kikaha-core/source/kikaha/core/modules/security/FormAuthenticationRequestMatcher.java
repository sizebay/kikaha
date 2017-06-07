package kikaha.core.modules.security;

import javax.inject.*;
import java.net.*;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.extern.slf4j.Slf4j;

/**
 * An {@link AuthenticationRequestMatcher} that matches if the current request should
 * execute an {@link AuthenticationMechanism}.
 */
@Slf4j
@Singleton
public class FormAuthenticationRequestMatcher implements AuthenticationRequestMatcher {

	@Inject DefaultAuthenticationConfiguration defaultAuthenticationConfiguration;

	@Override
	public boolean matches( HttpServerExchange exchange ) {
		final String url = exchange.getRelativePath();
		return  ( defaultAuthenticationConfiguration.getCallbackUrl().equals( url )
			   || ( !isUrlFromAuthenticationResources( url ) ) );
	}

	private boolean isUrlFromAuthenticationResources( final String url ) {
		return  defaultAuthenticationConfiguration.getErrorPage().equals( url )
			||  defaultAuthenticationConfiguration.getLoginPage().equals( url );
	}
}
