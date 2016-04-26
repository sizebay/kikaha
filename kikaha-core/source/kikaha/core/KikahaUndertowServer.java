package kikaha.core;

import io.undertow.Undertow;
import kikaha.core.cdi.Application;
import kikaha.core.modules.ModuleLoader;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Slf4j
@Getter
@Accessors(fluent = true)
@Singleton
public class KikahaUndertowServer implements Application {

	@Inject
	DeploymentContext deploymentContext;

	@Inject
	ModuleLoader loader;

	Undertow server;

	@Override
	public void run() throws Exception {
		try {
			configureUndertow();
			start();
		} catch ( Throwable anyProblem ) {
			log.error( "Could not start server", anyProblem );
			System.exit( 1 );
		}
	}

	void configureUndertow() throws IOException {
		Runtime.getRuntime().addShutdownHook( new UndertowShutdownHook() );
		final Undertow.Builder undertow = Undertow.builder();
		loader.load( undertow, deploymentContext );
		undertow.setHandler( new DefaultHttpRequestHandler( deploymentContext ) );
		server = undertow.build();
	}

	void start(){
		server.start();
	}

	public void stop() {
		this.server.stop();
		log.info("Server stopped!");
	}

	class UndertowShutdownHook extends Thread {

		@Override
		public void run() {
			KikahaUndertowServer.this.stop();
		}
	}
}