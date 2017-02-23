package kikaha.core.modules.smart;

import javax.inject.*;
import java.io.IOException;
import java.util.*;
import io.undertow.Undertow.Builder;
import io.undertow.server.*;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;

/**
 * @author: miere.teixeira
 */
@Singleton
public class StaticHeaderModule implements Module {

	@Inject Config config;

	@Override
	public void load( final Builder server, final DeploymentContext context ) throws IOException {
		final List<Config> configList = config.getConfigList( "server.smart-routes.static-headers" );
		if ( configList != null )
			for ( final Config staticHeaderConfig : configList ) {
				final String url = staticHeaderConfig.getString( "url" );
				final Map<String, Object> headers = readHeadersFrom( staticHeaderConfig );
				final HttpHandler httpHandler = context.rootHandler();
				final HttpHandler staticHandler = StaticHeadersHttpHandler.create( httpHandler, url, headers );
				context.rootHandler( staticHandler );
			}
	}

	private Map<String, Object> readHeadersFrom( final Config staticHeaderConfig ) {
		final Config headers = staticHeaderConfig.getConfig( "headers" );
		if ( headers != null )
			return headers.toMap();
		return null;
	}
}
