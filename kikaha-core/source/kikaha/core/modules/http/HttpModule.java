package kikaha.core.modules.http;

import io.undertow.Undertow;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 */
@Slf4j
@Getter
@Singleton
public class HttpModule implements Module {

	final String name = "http";

	@Inject
	Config config;

	@Override
	public void load(Undertow.Builder server, DeploymentContext context) {
		Config httpConfig = config.getConfig("server.http");
		if ( httpConfig.getBoolean("enabled") )
			loadHttpListener( httpConfig, server );
	}

	void loadHttpListener( Config httpConfig, Undertow.Builder server ){
		final int port = httpConfig.getInteger("port");
		final String host = httpConfig.getString("host");
		log.info( "Listening for HTTP requests at " + host + ":" + port );
		server.addHttpListener(port, host);
	}
}
