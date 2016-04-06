package kikaha.core.modules;

import io.undertow.Undertow;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 */
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
		server.addHttpListener(
				httpConfig.getInteger("port"),
				httpConfig.getString("host")
		);
	}
}
