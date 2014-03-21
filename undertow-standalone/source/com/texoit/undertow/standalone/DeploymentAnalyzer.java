package com.texoit.undertow.standalone;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

import com.texoit.undertow.standalone.api.Configuration;
import com.texoit.undertow.standalone.api.DeploymentContext;
import com.texoit.undertow.standalone.api.DeploymentHook;
import com.texoit.undertow.standalone.api.UndertowStandaloneException;
import com.texoit.undertow.standalone.api.Path;
import com.texoit.undertow.standalone.api.RequestHook;

@Log
@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class DeploymentAnalyzer {

	@Getter( lazy=true )
	private final LibraryClassPathImporter availableClasses = new LibraryClassPathImporter( configuration() );
	private final Collection<RequestHook> hooks = new ArrayList<>();
	private final Collection<DeploymentHook> deploymentHooks = new ArrayList<>();
	private final PathHandler uris = Handlers.path();

	private final Configuration configuration;

	public DeploymentContext analyze() throws UndertowStandaloneException {
		try {
			Collection<Class<?>> retrievedAvailableClasses = availableClasses().retrieve();
			analyze( retrievedAvailableClasses );
			memorizeDefaultResourceHandler();
			return DefaultDeploymentContext.with( this.hooks(), this.deploymentHooks(), this.uris(), retrievedAvailableClasses );
		} catch (IOException e) {
			throw new UndertowStandaloneException(e);
		}
	}

	public void memorizeDefaultResourceHandler() {
		this.uris.addPrefixPath( "/", createResourceManager() );
		this.hooks.add( ResourceHandlerHook.wrap( this.uris ) );
	}

	public HttpHandler createResourceManager() {
		File location = retrieveWebAppFolder();
		FileResourceManager resourceManager = new FileResourceManager( location, 100 );
		log.info( "Exposing resource files at " + location );
		return new ResourceHandler()
				.setResourceManager( resourceManager )
				.setDirectoryListingEnabled( true );
	}

	public File retrieveWebAppFolder() {
		File location = new File( configuration().resourcesPath() );
		if ( !location.exists() )
			location.mkdir();
		return location;
	}

	public void analyze( Collection<Class<?>> availableClasses ) {
		for ( Class<?> clazz : availableClasses )
			try {
				tryMemorizeClass( clazz );
			} catch ( UndertowStandaloneException cause ) {
				cause.printStackTrace();
			}
	}

	@SuppressWarnings( "unchecked" )
	private void tryMemorizeClass( Class<?> clazz ) throws UndertowStandaloneException {
		if ( isHttpHandler( clazz ) )
			memorizeHandler( (Class<? extends HttpHandler>)clazz );
		else if ( isHook( clazz ) )
			memorizeHook( (Class<? extends RequestHook>)clazz );
		else if ( isDeploymentHook( clazz ) )
			memorizeDeploymentHook( (Class<? extends DeploymentHook>)clazz );
	}

	private boolean isHttpHandler( Class<?> clazz ) {
		return HttpHandler.class.isAssignableFrom( clazz )
				&& clazz.isAnnotationPresent( Path.class );
	}

	private void memorizeHandler( Class<? extends HttpHandler> clazz ) throws UndertowStandaloneException {
		HttpHandler instance = instantiate( clazz );
		Path path = clazz.getAnnotation( Path.class );
		log.info( "Registering " + instance.getClass().getCanonicalName() + " at " + path.value() + "." );
		this.uris.addPrefixPath( path.value(), instance );
	}

	private boolean isHook( Class<?> clazz ) {
		return RequestHook.class.isAssignableFrom( clazz )
				&& !ResourceHandlerHook.class.equals( clazz )
				&& !clazz.isInterface();
	}

	private void memorizeHook( Class<? extends RequestHook> clazz ) throws UndertowStandaloneException {
		RequestHook instance = instantiate( clazz );
		log.info( "Registering request hook " + instance.getClass().getCanonicalName() + "." );
		this.hooks.add( instance );
	}

	private boolean isDeploymentHook( Class<?> clazz ) {
		return DeploymentHook.class.isAssignableFrom( clazz )
				&& !clazz.isInterface();
	}

	private void memorizeDeploymentHook( Class<? extends DeploymentHook> clazz ) throws UndertowStandaloneException {
		DeploymentHook instance = instantiate( clazz );
		this.deploymentHooks.add( instance );
	}

	private <T> T instantiate( Class<T> clazz ) throws UndertowStandaloneException {
		try {
			return clazz.newInstance();
		} catch ( InstantiationException | IllegalAccessException cause ) {
			throw new UndertowStandaloneException( cause );
		}
	}
}
