package kikaha.core.websocket;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketConnectionCallback;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import kikaha.core.api.WebResource;
import lombok.extern.java.Log;
import trip.spi.ProvidedServices;
import trip.spi.Singleton;

@Log
@Singleton( exposedAs = DeploymentHook.class )
public class WebSocketDeploymentHook implements DeploymentHook {

	@ProvidedServices( exposedAs = WebSocketHandler.class )
	Iterable<WebSocketHandler> handlers;

	@Override
	public void onDeploy( final DeploymentContext context ) {
		log.info( "Deploying WebSocket resources..." );
		for ( final WebSocketHandler handler : handlers )
			deploy( context, handler );
	}

	void deploy( final DeploymentContext context, final WebSocketHandler handler ) {
		final WebResource webResource = handler.getClass().getAnnotation( WebResource.class );
		if ( webResource == null ) {
			log.warning( "No WebResource annotation found for " + handler.getClass().getCanonicalName() + ": Skipped!" );
			return;
		}
		context.register( webResource.value(), "GET", wrappedWebsocketHandlerFrom( handler ) );
	}

	HttpHandler wrappedWebsocketHandlerFrom( final WebSocketHandler handler ) {
		final WebSocketConnectionCallback callbackHandler = new WebSocketConnectionCallbackHandler( handler );
		return Handlers.websocket( callbackHandler );
	}

	@Override
	public void onUndeploy( final DeploymentContext context ) {
	}
}
