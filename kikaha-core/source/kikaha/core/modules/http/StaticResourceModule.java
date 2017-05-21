package kikaha.core.modules.http;

import java.io.*;
import javax.inject.*;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.*;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
			final String location = staticConfig.getString("location");
			log.info( "Enabling static routing at folder: " + location );
			final ResourceManager resourceManager = loadResourceManagerFor( location );
			setStaticRoutingAsFallbackHandler(resourceManager, context);
		}
	}

	private ResourceManager loadResourceManagerFor(String location) {
		final File locationAsFile = new File(location);
		if ( locationAsFile.exists() )
			return new FileResourceManager( locationAsFile, 100 );
		final ClassLoader classLoader = StaticResourceModule.class.getClassLoader();
		return new ClassPathResourceManager( classLoader, location );
	}

	void setStaticRoutingAsFallbackHandler( ResourceManager resourceManager, DeploymentContext context ){
		final HttpHandler fallbackHandler = context.fallbackHandler();
		final ResourceHandler handler = new ResourceHandler(resourceManager, fallbackHandler);
		context.fallbackHandler( handler );
	}
}
