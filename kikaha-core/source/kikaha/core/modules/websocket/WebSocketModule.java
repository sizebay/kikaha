package kikaha.core.modules.websocket;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketConnectionCallback;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.core.modules.http.WebResource;
import kikaha.core.url.URL;
import kikaha.core.url.URLMatcher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class WebSocketModule implements Module {

	@Getter
	final String name = "websocket";

	@Inject
	@Typed( WebSocketHandler.class )
	Iterable<WebSocketHandler> handlers;

	@Override
	public void load( Undertow.Builder server, final DeploymentContext context ) {
		for ( final WebSocketHandler handler : handlers )
			deploy( context, handler );
	}

	void deploy( final DeploymentContext context, final WebSocketHandler handler ) {
		final WebResource webResource = handler.getClass().getAnnotation( WebResource.class );
		if ( webResource == null ) {
			log.warn( "No WebResource annotation found for " + handler.getClass().getCanonicalName() + ": Skipped!" );
			return;
		}
		context.register( webResource.path(), "GET", wrappedWebsocketHandlerFrom( handler, webResource ) );
	}

	HttpHandler wrappedWebsocketHandlerFrom( final WebSocketHandler handler, final WebResource webResource ) {
		final String url = URL.removeTrailingCharacter( webResource.path() );
		final URLMatcher urlMatcher = URLMatcher.compile( "{protocol}://{host}" + url );
		final WebSocketConnectionCallback callbackHandler = new WebSocketConnectionCallbackHandler( handler, urlMatcher );
		return Handlers.websocket( callbackHandler );
	}

}