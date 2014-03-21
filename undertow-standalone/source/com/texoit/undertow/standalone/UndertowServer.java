package com.texoit.undertow.standalone;

import io.undertow.Undertow;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import com.texoit.undertow.standalone.api.DeploymentContext;
import com.texoit.undertow.standalone.api.DeploymentHook;
import com.texoit.undertow.standalone.api.DrowningException;

@Log
@RequiredArgsConstructor
public class UndertowServer {

	final String host;
	final int port;

	DeploymentContext deploymentContext;
	Undertow server;

	public void start() throws DrowningException {
		bootstrap();
		this.server = createServer();
		this.server.start();
		log.info( "Server was started listening at " + host + ":" + port );
	}

	public void bootstrap() throws DrowningException {
		DeploymentAnalyzer deploymentAnalyzer = new DeploymentAnalyzer();
		this.deploymentContext = deploymentAnalyzer.analyze();
		doDeploy( this.deploymentContext );
	}

	private void doDeploy( DeploymentContext deploymentContext ) {
		for ( DeploymentHook hook : deploymentContext.deploymentHooks() ) {
			log.info( "Dispatching deployment hook: " + hook.getClass().getCanonicalName() );
			hook.onDeploy( deploymentContext );
		}
	}

	private Undertow createServer() {
		return Undertow.builder()
				.addHttpListener( this.port, this.host )
				.setHandler( new DefaultHttpRequestHandler( this.deploymentContext ) )
				.build();
	}

	public void stop() {
		this.server.stop();
		log.info( "Server stopped!" );
	}
}
