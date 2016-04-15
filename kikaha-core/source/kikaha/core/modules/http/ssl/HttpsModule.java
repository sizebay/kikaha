package kikaha.core.modules.http.ssl;

import io.undertow.Undertow;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import java.io.IOException;

/**
 *
 */
@Slf4j
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

	private void deployHttpToHttps(DeploymentContext context) {
		if ( config.getBoolean("server.https.redirect-http-to-https") ) {
			log.info("Redireting HTTP requests to HTTPS");
			context.rootHandler(new AutoHTTPSRedirectHandler(context.rootHandler()));
		}
	}
}
