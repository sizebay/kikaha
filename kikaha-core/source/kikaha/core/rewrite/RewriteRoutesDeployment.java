package kikaha.core.rewrite;

import io.undertow.server.handlers.ResponseCodeHandler;
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
			context.register( new RewriteRequestHook( new Rewriter( route ) ) );
		}
	}

	void deployReverseProxyRoutes( final DeploymentContext context )
	{
		val routes = configuration.routes().reverseProxyRoutes();
		for ( val route : routes ) {
			log.info( "Deploying reverse proxy rule: " + route );
			val proxyClient = new RewriterProxyClientProvider( new Rewriter( route ) );
			val handler = new ProxyHandler( proxyClient, ResponseCodeHandler.HANDLE_404 );
			context.register( route.path(), handler );
		}
	}

	@Override
	public void onUndeploy( final DeploymentContext context )
	{
	}
}
