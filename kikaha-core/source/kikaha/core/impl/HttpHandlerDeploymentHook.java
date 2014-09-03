package kikaha.core.impl;

import io.undertow.server.HttpHandler;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import kikaha.core.api.WebResource;
import lombok.extern.java.Log;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

@Log
@Singleton( exposedAs = DeploymentHook.class )
public class HttpHandlerDeploymentHook implements DeploymentHook {
	
	@Provided ServiceProvider provider;

	@Override
	public void onDeploy(DeploymentContext context) {
		try {
			log.info("Looking for HttpHandler routes...");
			deployAllHttpHandlers( context );
		} catch ( ServiceProviderException cause ) {
			handleFailure(cause);
		}
	}

	void deployAllHttpHandlers( DeploymentContext context ) throws ServiceProviderException {
		Iterable<HttpHandler> handlers = provider.loadAll( HttpHandler.class );
		for ( HttpHandler handler : handlers )
			deploy(context, handler);
	}

	void deploy(DeploymentContext context, HttpHandler handler) {
		WebResource webResource = handler.getClass().getAnnotation( WebResource.class );
		if ( webResource == null ){
			log.warning( "No WebResource annotation found for " + handler.getClass().getCanonicalName() + ": Skipped!" );
			return;
		}
		context.register(webResource.value(), webResource.method(), handler);
	}

	void handleFailure(ServiceProviderException cause) {
		log.severe( cause.getMessage() );
		cause.printStackTrace();
	}

	@Override
	public void onUndeploy(DeploymentContext context) {
	}

}
