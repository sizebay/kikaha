package kikaha.core.rewrite;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.proxy.ProxyHandler;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import kikaha.core.api.conf.Configuration;
import lombok.val;
import lombok.extern.java.Log;
import trip.spi.Provided;
import trip.spi.Singleton;

@Log
@Singleton( exposedAs = DeploymentHook.class )
public class RewriteRoutesDeployment
	implements DeploymentHook {

	static final HttpHandler HANDLER_404 = new NotFoundHandler();

	@Provided
	Configuration configuration;

	@Override
	public void onDeploy( final DeploymentContext context )
	{
		deployRewriteRoutes( context );
		deployReverseProxyRoutes( context );
	}

	void deployRewriteRoutes( final DeploymentContext context )
	{
		val routes = configuration.routes().rewriteRoutes();
		for ( val route : routes ) {
			log.info( "Deploying rewrite rule: " + route );
			context.register( RewriteRequestHook.from( route ) );
		}
	}

	void deployReverseProxyRoutes( final DeploymentContext context )
	{
		val routes = configuration.routes().reverseProxyRoutes();
		for ( val route : routes ) {
			log.info( "Deploying reverse proxy rule: " + route );
			val proxyClient = RewriterProxyClientProvider.from( route );
			val handler = new ProxyHandler( proxyClient, HANDLER_404 );
			context.register( route.path(), handler );
		}
	}

	@Override
	public void onUndeploy( final DeploymentContext context )
	{
	}
}

@Log
class NotFoundHandler implements HttpHandler {

	@Override
	public void handleRequest( final HttpServerExchange exchange ) throws Exception
	{
		exchange.setResponseCode( 404 );
		exchange.endExchange();
		log.info( "Exchange ended with 404 status" );
	}

}