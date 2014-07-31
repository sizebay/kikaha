package kikaha.core;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Methods;

import java.util.*;

import kikaha.core.api.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class DefaultDeploymentContext implements DeploymentContext {

	final Iterable<DeploymentHook> deploymentHooks;
	final List<RequestHook> requestHooks;
	final RoutingHandler rootHandler = Handlers.routing();
	final Map<String, Object> attributes = new HashMap<String, Object>();

	@Override
	public DeploymentContext register( RequestHook hook ) {
		this.requestHooks.add( hook );
		return this;
	}

	@Override
	public DeploymentContext register( String uri, HttpHandler handler ) {
		return register( uri, Methods.GET_STRING, handler );
	}

	@Override
	public DeploymentContext register( String uri, String method, HttpHandler handler ) {
		log.info( "Registering route: " + method + ":" + uri + "." );
		this.rootHandler.add( method, uri, handler );
		return this;
	}

	@Override
	public <T> DeploymentContext attribute( Class<T> clazz, T object ) {
		return attribute( clazz.getCanonicalName(), object );
	}

	@Override
	public DeploymentContext attribute( String key, Object object ) {
		attributes.put( key, object );
		return this;
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public <T> T attribute( Class<T> clazz ) {
		return (T)attribute( clazz.getCanonicalName() );
	}

	@Override
	public Object attribute( String key ) {
		return attributes.get( key );
	}

	@Override
	public DeploymentContext fallbackHandler( HttpHandler fallbackHandler ) {
		rootHandler.setFallbackHandler( fallbackHandler );
		return this;
	}
}
