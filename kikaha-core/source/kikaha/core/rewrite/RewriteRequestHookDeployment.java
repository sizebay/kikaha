package kikaha.core.rewrite;

import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import kikaha.core.api.conf.Configuration;
import lombok.val;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton( exposedAs = DeploymentHook.class )
public class RewriteRequestHookDeployment
	implements DeploymentHook {

	@Provided
	Configuration configuration;

	@Override
	public void onDeploy( final DeploymentContext context )
	{
		val routes = configuration.routes().rewriteRoutes();
		for ( val route : routes )
			context.register( new RewriteRequestHook( route ) );
	}

	@Override
	public void onUndeploy( final DeploymentContext context )
	{
	}
}
