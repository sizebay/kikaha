package kikaha.core;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import kikaha.core.url.URL;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultHttpRequestHandler implements HttpHandler {

	final DeploymentContext context;

	@Override
	public void handleRequest( final HttpServerExchange exchange ) throws Exception {
		fixRelativePath( exchange );
		context.rootHandler().handleRequest(exchange);
	}

	void fixRelativePath( final HttpServerExchange exchange ) {
		final String relativePath = URL.removeTrailingCharacter( exchange.getRelativePath() );
		exchange.setRelativePath( relativePath );
	}
}