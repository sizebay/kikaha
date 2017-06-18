package kikaha.core.modules.http;

import java.io.IOException;
import javax.inject.*;
import io.undertow.Undertow;
import io.undertow.server.*;
import io.undertow.server.handlers.resource.*;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.core.url.URLMatcher;
import kikaha.core.util.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Getter
@Slf4j
@Singleton
public class StaticResourceModule implements Module {

	public static final String DEFAULT_WEBJAR_LOCATION = "META-INF/resources/webjars/";

	@Inject
	Config config;

	@Override
	public void load(Undertow.Builder server, DeploymentContext context) throws IOException {
		final Config staticConfig = config.getConfig("server.static");
		if ( staticConfig.getBoolean("enabled") ) {
			deployDefaultStaticRouting(staticConfig, context);
			deployWebJarStaticRouting(staticConfig, context);
		}
	}

	private void deployWebJarStaticRouting(Config staticConfig, DeploymentContext context) {
		if ( staticConfig.getBoolean("webjar-enabled" ) ) {
			final String urlPrefix = staticConfig.getString("webjar-url-prefix").replaceFirst( "/$", "" );
			log.info( "Enabling static routing for webjars at '" + urlPrefix + "'" );
			final String webJarInternalLocation = staticConfig.getString("webjar-location", DEFAULT_WEBJAR_LOCATION);
			final ResourceManager resourceManager = SystemResource.loadResourceManagerFor( webJarInternalLocation );
			final HttpHandler webjarHandler = new WebJarHttpHandler(
				new ResourceHandler(resourceManager, context.rootHandler() ),
				URLMatcher.compile( urlPrefix + "/{path}" ) );
			context.rootHandler( webjarHandler );
		}
	}

	private void deployDefaultStaticRouting(final Config staticConfig, DeploymentContext context){
		final String location = staticConfig.getString("location");
		log.info( "Enabling static routing at folder: " + location );
		final ResourceManager resourceManager = SystemResource.loadResourceManagerFor( location );
		setStaticRoutingAsFallbackHandler(resourceManager, context);
	}

	void setStaticRoutingAsFallbackHandler( ResourceManager resourceManager, DeploymentContext context ){
		final HttpHandler fallbackHandler = context.fallbackHandler();
		final ResourceHandler handler = new ResourceHandler(resourceManager, fallbackHandler);
		context.fallbackHandler( handler );
	}
}

@RequiredArgsConstructor
class WebJarHttpHandler implements HttpHandler {

	final ResourceHandler handler;
	final URLMatcher uri;

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		final LastValueOnlyMap<String,String> matcher = new LastValueOnlyMap<>();
		uri.matches( exchange.getRelativePath(), matcher );
		exchange.setRelativePath( matcher.getValue() );
		handler.handleRequest( exchange );
	}
}