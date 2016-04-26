package kikaha.core;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

/**
 *
 */
@Singleton
@Typed(NotFoundHandler.class)
public class NotFoundHandler implements HttpHandler {

	@Override
	public void handleRequest( HttpServerExchange exchange ) throws Exception {
		exchange.setStatusCode( 404 );
		exchange.endExchange();
	}
}
