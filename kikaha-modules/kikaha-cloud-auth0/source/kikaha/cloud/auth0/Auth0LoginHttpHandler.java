package kikaha.cloud.auth0;

import static kikaha.cloud.auth0.Auth0Authentication.NONCE;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.Map.Entry;
import io.undertow.server.*;
import io.undertow.util.*;
import kikaha.core.*;
import kikaha.core.modules.security.*;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class Auth0LoginHttpHandler implements HttpHandler {

	static final String
		CONTENT_TYPE_JSON = "text/html",
		FAILURE_MSG = "Can't send message: "
	;

	@Inject Auth0.AuthConfig authConfig;
	ByteBuffer html;

	@PostConstruct
	public void preComputeHTML(){
		final String template = readAndParseTemplate();
		this.html = ByteBuffer.wrap( template.getBytes() );
	}

	String readAndParseTemplate(){
		final ChainedMap<String, String> templateVariables = ChainedMap.with( "clientId", authConfig.clientId )
				.and( "clientDomain", authConfig.clientDomain )
				.and( "authenticationCallbackUrl", authConfig.authenticationCallbackUrl );

		String template = SystemResource.readFileAsString( authConfig.loginTemplatePage, "UTF-8" );
		for ( final Entry<String, String> var : templateVariables.entrySet() )
			template = template.replace( "{{"+ var.getKey() +"}}", var.getValue() );

		return template;
	}

	@Override
	public void handleRequest( final HttpServerExchange exchange ) throws Exception
	{
		try {
			final SecurityContext securityContext = (SecurityContext) exchange.getSecurityContext();
			final Session session = securityContext.getCurrentSession();
			session.setAttribute( NONCE, "" );

			exchange.setStatusCode( StatusCodes.OK );
			exchange.getResponseHeaders().put( Headers.CONTENT_TYPE, CONTENT_TYPE_JSON );
			exchange.getResponseSender().send( html.toString() );
		} catch ( Throwable cause ) {
			handleFailure( exchange, cause );
		}
	}

	private void handleFailure( HttpServerExchange exchange, Throwable cause ) {
		log.error( FAILURE_MSG, cause );
		if ( !exchange.isResponseStarted() ) {
			exchange.setStatusCode( StatusCodes.OK );
			exchange.getResponseHeaders().put( Headers.CONTENT_TYPE, CONTENT_TYPE_JSON );
			exchange.getResponseSender().send( FAILURE_MSG );
		}
	}
}
