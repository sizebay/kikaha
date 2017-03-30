package kikaha.core.modules.security.login;

import java.util.*;
import java.util.Map.Entry;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import io.undertow.server.*;
import io.undertow.util.*;
import kikaha.config.Config;
import kikaha.core.SystemResource;
import kikaha.core.modules.security.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class AuthLoginHttpHandler implements HttpHandler {

	static final String
			CONTENT_HTML = "text/html",
		FAILURE_MSG = "Can't send message: "
	;

	@Inject Config config;

	@Inject
	@Typed( ConfigurationHook.class )
	Iterable<ConfigurationHook> configurationHooks;

	@Getter( lazy = true )
	private final String html = readAndParseTemplate();

	String readAndParseTemplate(){
		final Map<String, Object> templateVariables = readTemplateVariables();
		final String loginTemplatePage = config.getString( "server.auth.login-template", "default-login.html" );

		String template = SystemResource.readFileAsString( loginTemplatePage, "UTF-8" );
		for ( final Entry<String, Object> var : templateVariables.entrySet() )
			template = template.replace( "{{"+ var.getKey() +"}}", var.getValue().toString() );

		return template;
	}

	private Map<String, Object> readTemplateVariables(){
		final Map<String, Object> templateVariables = new HashMap<>();
		for ( final ConfigurationHook hook : configurationHooks ) {
			final Map<String, Object> extraParameters = hook.getExtraParameters();
			if ( extraParameters != null )
				templateVariables.putAll(extraParameters);
		}
		return templateVariables;
	}

	@Override
	public void handleRequest( final HttpServerExchange exchange ) throws Exception
	{
		try {
			final SecurityContext securityContext = (SecurityContext) exchange.getSecurityContext();
			final Session session = securityContext.getCurrentSession();
			for ( ConfigurationHook hook : configurationHooks )
				hook.configure( exchange, session );

			exchange.setStatusCode( StatusCodes.OK );
			exchange.getResponseHeaders().put( Headers.CONTENT_TYPE, CONTENT_HTML );
			exchange.getResponseSender().send( getHtml() );
		} catch ( Throwable cause ) {
			handleFailure( exchange, cause );
		}
	}

	private void handleFailure( HttpServerExchange exchange, Throwable cause ) {
		log.error( FAILURE_MSG, cause );
		if ( !exchange.isResponseStarted() ) {
			exchange.setStatusCode( StatusCodes.OK );
			exchange.getResponseHeaders().put( Headers.CONTENT_TYPE, CONTENT_HTML );
			exchange.getResponseSender().send( FAILURE_MSG );
		}
	}

	/**
	 * Configure how the Login Page should behave.
	 */
	public interface ConfigurationHook {

		/**
		 * Retrieve extra parameters that should be available for injection on the template.
		 * @return
		 */
		Map<String, Object> getExtraParameters();

		/**
		 * Configure the login request.
		 *
		 * @param exchange
		 * @param session
		 */
		void configure( HttpServerExchange exchange, Session session );
	}
}
