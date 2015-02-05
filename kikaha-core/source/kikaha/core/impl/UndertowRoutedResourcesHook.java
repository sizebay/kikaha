package kikaha.core.impl;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import kikaha.core.api.RequestHook;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.KikahaException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( staticName = "wrap" )
public class UndertowRoutedResourcesHook implements RequestHook {

	final HttpHandler resourceHandler;

	@Override
	public void execute( final RequestHookChain chain, final HttpServerExchange exchange )
			throws KikahaException {
		try {
			this.resourceHandler.handleRequest( exchange );
		} catch ( final Exception cause ) {
			throw new KikahaException( cause );
		}
	}
}