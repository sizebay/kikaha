package kikaha.core.modules.http;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 *
 */
@Slf4j
@Getter
@Singleton
public class HttpHandlerDeploymentModule implements Module {

	@Inject
	@Typed(HttpHandler.class)
	Iterable<HttpHandler> handlers;

	@Inject
	@Typed(HttpHandlerDeploymentCustomizer.class)
	Iterable<HttpHandlerDeploymentCustomizer> customizers;

	@Override
	public void load(Undertow.Builder server, DeploymentContext context) throws IOException {
		for (HttpHandler handler : handlers) {
			final WebResource resource = handler.getClass().getAnnotation(WebResource.class);
			if ( resource == null ) {
				log.warn( "Invalid web resource: " + handler.getClass().getCanonicalName() );
				continue;
			}
			register( context, handler, resource );
		}
	}

	void register( final DeploymentContext context, HttpHandler handler, WebResource resource ){
		HttpHandler newHandler;
		//if ( customizers != null )
			for ( final HttpHandlerDeploymentCustomizer customizer : customizers ) {
				newHandler = customizer.customize(handler, resource);
				if ( newHandler != null )
					handler = newHandler;
			}
		context.register( resource.path(), resource.method(), handler );
	}

	/**
	 * Allow developers to be notified about the deployment of a specific {@link HttpHandler}
	 * and take some action with this - e.g. wrap the HttpHandler, etc.
	 */
	public interface HttpHandlerDeploymentCustomizer {

		HttpHandler customize( HttpHandler httpHandler, WebResource webResource );
	}
}
