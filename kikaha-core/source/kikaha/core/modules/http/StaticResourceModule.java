package kikaha.core.modules.http;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

/**
 *
 */
@Getter
@Slf4j
@Singleton
public class StaticResourceModule implements Module {

	@Inject
	Config config;

	@Override
	public void load(Undertow.Builder server, DeploymentContext context) throws IOException {
		final Config staticConfig = config.getConfig("server.static");
		if ( staticConfig.getBoolean("enabled") ) {
			final File location = retrieveWebAppFolder(staticConfig);
			log.info( "Enabling static routing at folder: " + location.getAbsolutePath() );
			setStaticRoutingAsFallbackHandler(location, context);
		}
	}

	File retrieveWebAppFolder( Config staticConfig ) {
		final File location = new File( staticConfig.getString("location") );
		if (!location.exists())
			log.error( "Folder not found: " + location.getAbsolutePath() );
		return location;
	}

	void setStaticRoutingAsFallbackHandler( File location, DeploymentContext context ){
		final FileResourceManager resourceManager = new FileResourceManager(location, 100);
		final HttpHandler fallbackHandler = context.fallbackHandler();
		final ResourceHandler handler = new ResourceHandler(resourceManager, fallbackHandler);
		context.fallbackHandler( handler );
	}
}
