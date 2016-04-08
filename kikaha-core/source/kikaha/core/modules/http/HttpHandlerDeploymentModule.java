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

	final String name = "http-handler-deployment";

	@Inject
	@Typed(HttpHandler.class)
	Iterable<HttpHandler> handlers;

	@Override
	public void load(Undertow.Builder server, DeploymentContext context) throws IOException {
		for (HttpHandler handler : handlers) {
			final WebResource resource = handler.getClass().getAnnotation(WebResource.class);
			if ( resource == null ) {
				log.warn( "Invalid web resource: " + handler.getClass().getCanonicalName() );
				continue;
			}
			context.register( resource.path(), resource.method(), handler );
		}
	}
}
