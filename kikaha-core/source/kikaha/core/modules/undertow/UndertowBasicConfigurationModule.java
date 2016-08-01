package kikaha.core.modules.undertow;

import static kikaha.core.modules.undertow.UndertowServerOptionsConfiguration.getConfigOption;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.inject.*;
import io.undertow.Undertow.Builder;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.core.modules.undertow.UndertowServerOptionsConfiguration.ConfigurableOption;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Singleton
@Getter
public class UndertowBasicConfigurationModule implements Module {

	final String name = "undertow-basic-configuration";

	@Inject
	Config config;

	Config undertowConfig;

	@PostConstruct
	public void loadUndertowConfig(){
		undertowConfig = config.getConfig("server.undertow");
	}

	@Override
	public void load(Builder builder, DeploymentContext context) throws IOException {
		log.info("Configuring Undertow options...");
		setValues(builder);
		setServerOptions(builder);
		setSocketOptions(builder);
	}

	private void setValues(final Builder builder) {
		setIOThreads(builder);
		setWorkerThreads(builder);
		setBufferSize(builder);
	}

	private void setIOThreads(final Builder builder) {
		final int ioThreads = undertowConfig.getInteger("io-threads");
		if ( ioThreads > 0 ) {
			log.info("  io-threads: " + ioThreads );
			builder.setIoThreads(ioThreads);
		}
	}

	private void setWorkerThreads(final Builder builder) {
		final int workerThreads = undertowConfig.getInteger("worker-threads");
		if ( workerThreads > 0 ){
			log.info("  worker-threads: " + workerThreads );
			builder.setWorkerThreads(workerThreads);
		}
	}

	private void setBufferSize(final Builder builder) {
		final int bufferSize = undertowConfig.getInteger("buffer-size");
		log.info("  buffer-size: " + bufferSize );
		builder.setBufferSize(bufferSize);
	}

	private void setServerOptions(final Builder builder) {
		Config serverOptions = undertowConfig.getConfig( "server-options" );
		for ( String key : serverOptions.getKeys() ) {
			ConfigurableOption option = getConfigOption( key, serverOptions.getObject(key) );
			if ( option != null ) {
				log.info( "  " + option.getOption() + ": " + option.getValue() );
				builder.setServerOption(option.getOption(), option.getValue());
			}
		}
	}

	private void setSocketOptions(final Builder builder) {
		Config socketOptions = undertowConfig.getConfig( "server.undertow.socket-options" );
		for ( String key : socketOptions.getKeys() ) {
			ConfigurableOption option = getConfigOption( key, socketOptions.getObject(key) );
			if ( option != null ) {
				log.info( "  " + option.getOption() + ": " + option.getValue() );
				builder.setSocketOption(option.getOption(), option.getValue());
			}
		}
	}
}