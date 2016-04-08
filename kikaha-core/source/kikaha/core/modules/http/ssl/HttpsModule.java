package kikaha.core.modules.http.ssl;

import io.undertow.Undertow;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import java.io.IOException;

/**
 *
 */
@Getter
@Singleton
public class HttpsModule implements Module {

	final String name = "https";

	@Inject
	Config config;

	@Inject
	SSLContextFactory sslContextFactory;

	@Override
	public void load(Undertow.Builder server, DeploymentContext context) throws IOException {
		Config httpConfig = config.getConfig("server.https");
		if ( httpConfig.getBoolean("enabled") )
			loadHttpsListener( httpConfig, server );
	}

	void loadHttpsListener( Config httpConfig, Undertow.Builder server ) throws IOException {
		final SSLContext sslContext = sslContextFactory.createSSLContext();
		server.addHttpsListener(
				httpConfig.getInteger("port"),
				httpConfig.getString("host"),
				sslContext
		);
	}
}
