package kikaha.core.impl;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import kikaha.core.api.RequestHook;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.UndertowStandaloneException;
import kikaha.core.url.URL;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( staticName = "wrap" )
public class UndertowRoutedResourcesHook implements RequestHook {

	final HttpHandler resourceHandler;

	@Override
	public void execute( final RequestHookChain chain, final HttpServerExchange exchange )
			throws UndertowStandaloneException {
		try {
			fixRelativePath( exchange );
			this.resourceHandler.handleRequest( exchange );
		} catch ( final Exception cause ) {
			throw new UndertowStandaloneException( cause );
		}
	}

	void fixRelativePath( final HttpServerExchange exchange ) {
		final String relativePath = URL.removeTrailingCharacter( exchange.getRelativePath() );
		exchange.setRelativePath( relativePath );
	}
}