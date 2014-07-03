package io.skullabs.undertow.standalone;

import lombok.extern.java.Log;
import io.skullabs.undertow.standalone.api.DeploymentContext;
import io.skullabs.undertow.standalone.api.DeploymentHook;
import io.skullabs.undertow.standalone.api.WebResource;
import io.undertow.server.HttpHandler;
import trip.spi.Provided;
import trip.spi.Service;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

@Log
@Service
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
