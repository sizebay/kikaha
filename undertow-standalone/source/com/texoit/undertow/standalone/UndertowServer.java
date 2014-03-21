package com.texoit.undertow.standalone;

import io.undertow.Undertow;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

import com.texoit.undertow.standalone.api.Configuration;
import com.texoit.undertow.standalone.api.DeploymentContext;
import com.texoit.undertow.standalone.api.DeploymentHook;
import com.texoit.undertow.standalone.api.UndertowStandaloneException;

@Log
@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class UndertowServer {

	private final Configuration configuration;
	private DeploymentContext deploymentContext;
	private Undertow server;

	public void start() throws UndertowStandaloneException {
		bootstrap();
		this.server = createServer();
		this.server.start();
		log.info( "Server was started listening at " + configuration().host() + ":" + configuration().port() );
	}

	public void bootstrap() throws UndertowStandaloneException {
		DeploymentAnalyzer deploymentAnalyzer = new DeploymentAnalyzer( configuration() );
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
				.addHttpListener( configuration().port(), configuration().host() )
				.setHandler( new DefaultHttpRequestHandler( this.deploymentContext() ) )
				.build();
	}

	public void stop() {
		this.server().stop();
		log.info( "Server stopped!" );
	}
}
