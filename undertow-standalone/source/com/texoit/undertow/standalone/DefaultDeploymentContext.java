package com.texoit.undertow.standalone;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.texoit.undertow.standalone.api.DeploymentContext;
import com.texoit.undertow.standalone.api.DeploymentHook;
import com.texoit.undertow.standalone.api.RequestHook;

@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor( staticName = "with" )
public class DefaultDeploymentContext implements DeploymentContext {

	final Collection<RequestHook> requestHooks;
	final Collection<DeploymentHook> deploymentHooks;
	final PathHandler uris;
	final Collection<Class<?>> availableClasses;
	final Map<Class<?>,Object> attributes = new HashMap<>();

	@Override
	public DeploymentContext register( RequestHook hook ) {
		this.requestHooks.add( hook );
		return this;
	}

	public DeploymentContext register( String uri, HttpHandler handler ) {
		this.uris.addPrefixPath( uri, handler );
		return this;
	}

	@Override
	public <T> DeploymentContext attribute(Class<T> clazz, T object) {
		attributes.put(clazz, object);
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T attribute(Class<T> clazz) {
		return (T) attributes.get(clazz);
	}
}
