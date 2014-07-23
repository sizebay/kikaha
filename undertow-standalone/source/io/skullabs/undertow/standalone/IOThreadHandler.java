package io.skullabs.undertow.standalone;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( staticName = "wrap" )
public class IOThreadHandler implements HttpHandler {

	final HttpHandler targetHandler;

	@Override
	public void handleRequest( HttpServerExchange exchange ) throws Exception {
		if ( !exchange.isInIoThread() )
			exchange.dispatch( this );
		else
			targetHandler.handleRequest( exchange );
	}
}
