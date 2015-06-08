package kikaha.core.impl;

import io.undertow.server.HttpHandler;
import io.undertow.util.Methods;

import java.util.HashMap;
import java.util.Map;

import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import kikaha.core.url.SimpleRoutingHandler;
import kikaha.core.url.URL;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class DefaultDeploymentContext implements DeploymentContext {

	final Map<String, Object> attributes = new HashMap<String, Object>();
	final SimpleRoutingHandler routingHandler = new SimpleRoutingHandler();
	
	@NonNull
	@Setter
	HttpHandler rootHandler = routingHandler;

	final Iterable<DeploymentHook> deploymentHooks;

	@Override
	public DeploymentContext register( final String uri, final HttpHandler handler ) {
		register( uri, "PATCH", handler );
		register( uri, Methods.PUT_STRING, handler );
		register( uri, Methods.POST_STRING, handler );
		register( uri, Methods.DELETE_STRING, handler );
		return register( uri, Methods.GET_STRING, handler );
	}

	@Override
	public DeploymentContext register( String uri, final String method, final HttpHandler handler ) {
		uri = URL.removeTrailingCharacter( uri );
		log.info( "Registering route: " + method + ":" + uri + ": " + handler.getClass().getCanonicalName() );
		this.routingHandler.add( method, uri, handler );
		return this;
	}

	@Override
	public <T> DeploymentContext attribute( final Class<T> clazz, final T object ) {
		return attribute( clazz.getCanonicalName(), object );
	}

	@Override
	public DeploymentContext attribute( final String key, final Object object ) {
		attributes.put( key, object );
		return this;
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public <T> T attribute( final Class<T> clazz ) {
		return (T)attribute( clazz.getCanonicalName() );
	}

	@Override
	public Object attribute( final String key ) {
		return attributes.get( key );
	}

	@Override
	public DeploymentContext fallbackHandler( final HttpHandler fallbackHandler ) {
		routingHandler.setFallbackHandler( fallbackHandler );
		return this;
	}
}