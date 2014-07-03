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

	void fixRelativePath( HttpServerExchange exchange ) {
		final String relativePath = removeTrailingCharacter( exchange.getRelativePath() );
		exchange.setRelativePath( relativePath );
	}

	String removeTrailingCharacter( String original ) {
		final StringBuilder builder = new StringBuilder( original );
		while ( hasRemaningTrailingCharacter( builder ) )
			builder.deleteCharAt( builder.length() - 1 );
		return builder.toString();
	}

	boolean hasRemaningTrailingCharacter( final StringBuilder builder ) {
		return builder != null && builder.length() > 1 && '/' == builder.charAt( builder.length() - 1 );
	}
}