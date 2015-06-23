package kikaha.core.rewrite;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.proxy.ProxyHandler;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentListener;
import kikaha.core.api.conf.Configuration;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import trip.spi.Provided;
import trip.spi.Singleton;

@Slf4j
@Singleton( exposedAs = DeploymentListener.class )
public class RewriteRoutesDeployment
	implements DeploymentListener {

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
			val rewriteHandler = RewriteRequestHook.from( route, context.rootHandler() );
			context.rootHandler( rewriteHandler );
		}
	}

	void deployReverseProxyRoutes( final DeploymentContext context )
	{
		val routes = configuration.routes().reverseProxyRoutes();
		HttpHandler lastHandler = context.rootHandler();
		for ( val route : routes ) {
			log.info( "Deploying reverse proxy rule: " + route );
			val proxyClient = RewriterProxyClientProvider.from( route );
			lastHandler = new ProxyHandler( proxyClient, lastHandler );
		}
		context.rootHandler( lastHandler );
	}

	@Override
	public void onUndeploy( final DeploymentContext context )
	{
	}
}