package kikaha.core.modules.http;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.core.modules.http.WebResource;
import lombok.Getter;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 *
 */
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
			context.register( resource.path(), resource.method(), handler );
		}
	}
}
