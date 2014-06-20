package io.skullabs.undertow.standalone;

import io.skullabs.undertow.standalone.api.*;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( staticName = "wrap" )
public class UndertowRoutedResourcesHook implements RequestHook {

	final HttpHandler resourceHandler;

	@Override
	public void execute( RequestHookChain chain, HttpServerExchange exchange )
			throws UndertowStandaloneException {
		try {
			fixRelativePath( exchange );
			this.resourceHandler.handleRequest( exchange );
		} catch ( Exception cause ) {
			throw new UndertowStandaloneException( cause );
		}
	}

	private void fixRelativePath( HttpServerExchange exchange ) {
		String relativePath = exchange.getRelativePath();
		exchange.setRelativePath( relativePath.replaceFirst( "/+$", "" ) );
	}
}