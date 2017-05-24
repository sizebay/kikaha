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
		boolean matched = false;
		try {
			final String url = exchange.getRelativePath();
			final String referer = exchange.getRequestHeaders().getFirst( Headers.REFERER );
			final String refererPath = referer != null ? new URI( referer ).getPath() : "";
			matched = ( defaultAuthenticationConfiguration.getCallbackUrl().equals( url )
				   || ( !isUrlFromAuthenticationResources( url )
				   &&   !isUrlFromAuthenticationResources( refererPath ) ) );
		} catch ( URISyntaxException cause ) {
			log.error( "Can't execute FormAuthenticationRequestMatcher", cause );
		}
		return matched;
	}

	private boolean isUrlFromAuthenticationResources( final String url ) {
		return  defaultAuthenticationConfiguration.getErrorPage().equals( url )
			||  defaultAuthenticationConfiguration.getLoginPage().equals( url );
	}
}
