package kikaha.core.impl;

import io.undertow.server.HttpServerExchange;
import kikaha.core.api.RequestHook;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.KikahaException;
import kikaha.core.url.URL;

public class RelativePathFixerRequestHook implements RequestHook {

	@Override
	public void execute( final RequestHookChain chain, final HttpServerExchange exchange ) throws KikahaException
	{
		fixRelativePath( exchange );
		chain.executeNext();
	}

	void fixRelativePath( final HttpServerExchange exchange )
	{
		final String relativePath = URL.removeTrailingCharacter( exchange.getRelativePath() );
		exchange.setRelativePath( relativePath );
	}
}
