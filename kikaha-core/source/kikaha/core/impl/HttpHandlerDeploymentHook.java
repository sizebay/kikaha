package kikaha.core.impl;

import io.undertow.server.HttpHandler;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import kikaha.core.api.WebResource;
import lombok.extern.slf4j.Slf4j;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

@Slf4j
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
			log.warn( "No WebResource annotation found for " + handler.getClass().getCanonicalName() + ": Skipped!" );
			return;
		}
		context.register(webResource.value(), webResource.method(), handler);
	}

	void handleFailure(ServiceProviderException cause) {
		log.error( cause.getMessage() );
		cause.printStackTrace();
	}

	@Override
	public void onUndeploy(DeploymentContext context) {
	}

}
