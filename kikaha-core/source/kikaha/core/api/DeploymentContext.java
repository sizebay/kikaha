package kikaha.core.api;

import io.undertow.server.HttpHandler;

public interface DeploymentContext {

	Iterable<DeploymentListener> deploymentHooks();

	DeploymentContext register( final String uri, final HttpHandler handler );

	DeploymentContext register( final String uri, final String method, final HttpHandler handler );

	<T> DeploymentContext attribute( final Class<T> clazz, final T object );

	DeploymentContext attribute( final String key, final Object object );

	<T> T attribute( final Class<T> clazz );

	Object attribute( final String key );

	HttpHandler rootHandler();

	DeploymentContext rootHandler( final HttpHandler handler );

	DeploymentContext fallbackHandler( final HttpHandler fallbackHandler );
}
