package kikaha.core.api;

import io.undertow.server.HttpHandler;

public interface DeploymentContext {

	DeploymentContext register( RequestHook hook );

	Iterable<DeploymentHook> deploymentHooks();

	Iterable<RequestHook> requestHooks();

	DeploymentContext register( String uri, HttpHandler handler );

	DeploymentContext register( String uri, String method, HttpHandler handler );

	<T> DeploymentContext attribute( Class<T> clazz, T object );

	DeploymentContext attribute( String key, Object object );

	<T> T attribute( Class<T> clazz );

	Object attribute( String key );

	HttpHandler rootHandler();

	DeploymentContext fallbackHandler( HttpHandler fallbackHandler );
}
