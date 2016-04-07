package kikaha.core;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class NotFoundHandler implements HttpHandler {

	@Override
	public void handleRequest( HttpServerExchange exchange ) throws Exception {
		exchange.setStatusCode( 404 );
		exchange.endExchange();
	}
}
