package kikaha.core.impl;

import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.RequestHookChain;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class DefaultHttpRequestHandler implements HttpHandler {

	final DefaultResponseListener listener = new IgnoreUnhandledExchangesResponseListener();
	final DeploymentContext context;

	@Override
	public void handleRequest( final HttpServerExchange exchange ) throws Exception {
		exchange.addDefaultResponseListener( listener );
		final RequestHookChain chain = new DefaultRequestHookChain( exchange, context );
		chain.executeNext();
		System.out.println( "STATUS: " + exchange.getResponseCode() );
	}
}

class IgnoreUnhandledExchangesResponseListener implements DefaultResponseListener {

	@Override
	public boolean handleDefaultResponse( HttpServerExchange exchange ) {
		val id = Thread.currentThread().getId();
		System.out.println( id + ": " + exchange.getResponseCode() );
		return false;
	}
}