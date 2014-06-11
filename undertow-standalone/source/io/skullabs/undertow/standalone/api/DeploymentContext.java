package io.skullabs.undertow.standalone.api;

import io.undertow.server.HttpHandler;

public interface DeploymentContext {

	DeploymentContext register( RequestHook hook );

	Iterable<DeploymentHook> deploymentHooks();

	Iterable<RequestHook> requestHooks();

	DeploymentContext register( String uri, HttpHandler handler );

	<T> DeploymentContext attribute( Class<T> clazz, T object );

	DeploymentContext attribute( String key, Object object );

	<T> T attribute( Class<T> clazz );

	Object attribute( String key );
}
