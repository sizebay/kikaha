package io.skullabs.undertow.standalone;

import io.skullabs.undertow.standalone.api.DeploymentContext;
import io.skullabs.undertow.standalone.api.DeploymentHook;
import io.skullabs.undertow.standalone.api.RequestHook;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class DefaultDeploymentContext implements DeploymentContext {

	final Iterable<DeploymentHook> deploymentHooks;
	final List<RequestHook> requestHooks;
	final PathHandler uris = Handlers.path();
	final Map<String, Object> attributes = new HashMap<String, Object>();

	@Override
	public DeploymentContext register(RequestHook hook) {
		this.requestHooks.add(hook);
		return this;
	}

	@Override
	public DeploymentContext register(String uri, HttpHandler handler) {
		this.uris.addPrefixPath( uri, handler );
		return this;
	}

	@Override
	public <T> DeploymentContext attribute(Class<T> clazz, T object) {
		return attribute( clazz.getCanonicalName(), object );
	}

	@Override
	public DeploymentContext attribute(String key, Object object) {
		attributes.put(key, object);
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T attribute(Class<T> clazz) {
		return (T)attribute(clazz.getCanonicalName());
	}

	@Override
	public Object attribute(String key) {
		return attributes.get(key);
	}
}
