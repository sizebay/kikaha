package io.skullabs.undertow.standalone;

import io.skullabs.undertow.standalone.api.RequestHook;
import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
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
			this.resourceHandler.handleRequest( exchange );
		} catch ( Exception cause ) {
			throw new UndertowStandaloneException( cause );
		}
	}

}