package kikaha.cloud.auth0;

import static kikaha.cloud.auth0.Auth0Authentication.NONCE;

import javax.inject.Inject;
import java.util.Map;
import io.undertow.server.HttpServerExchange;
import kikaha.core.ChainedMap;
import kikaha.core.modules.security.Session;
import kikaha.core.modules.security.login.AuthLoginHttpHandler;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class Auth0LoginHttpHandler implements AuthLoginHttpHandler.ConfigurationHook {

	@Inject Auth0.AuthConfig authConfig;

	@Override
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getExtraParameters() {
		return (Map)ChainedMap.with( "clientId", authConfig.clientId )
				.and( "clientDomain", authConfig.clientDomain )
				.and( "authenticationCallbackUrl", authConfig.authenticationCallbackUrl );
	}

	@Override
	public void configure( HttpServerExchange exchange, Session session ) {
		session.setAttribute( NONCE, "" );
	}
}
