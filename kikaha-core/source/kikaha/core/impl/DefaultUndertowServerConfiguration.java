package kikaha.core.impl;

import static kikaha.core.impl.UndertowServerOptionsConfiguration.getConfigOption;

import com.typesafe.config.Config;

import io.undertow.Undertow.Builder;
import kikaha.core.api.UndertowServerConfiguration;
import kikaha.core.api.conf.Configuration;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import trip.spi.Provided;
import trip.spi.ProvidedServices;
import trip.spi.Singleton;

@Singleton
@SuppressWarnings("unchecked")
@Slf4j
public class DefaultUndertowServerConfiguration {

	@Provided
	Configuration kikahaConf;

	@ProvidedServices( exposedAs=UndertowServerConfiguration.class )
	Iterable<UndertowServerConfiguration> extraConfig;

	public void configure(final Builder builder) {
		log.info("Configuring Undertow options...");
		setValues(builder);
		setServerOptions(builder);
		setSocketOptions(builder);
		applyExtraConfigurationsFoundOnClassPath(builder);
	}

	private void setValues(final Builder builder) {
		val config = kikahaConf.config().getConfig("server.undertow");
		setIOThreads(builder, config);
		setWorkerThreads(builder, config);
		setBufferSize(builder, config);
	}

	private void setIOThreads(final Builder builder, final Config config) {
		val ioThreads = config.getInt("io-threads");
		if ( ioThreads > 0 )
			builder.setIoThreads(ioThreads);
	}

	private void setWorkerThreads(final Builder builder, final Config config) {
		val workerThreads = config.getInt("worker-threads");
		if ( workerThreads > 0 ){
			log.info("  > worker-threads: " + workerThreads );
			builder.setWorkerThreads(workerThreads);
		}
	}

	private void setBufferSize(final Builder builder, final Config config) {
		val bufferSize = config.getInt("buffer-size");
		log.info("  > buffer-size: " + bufferSize );
		builder.setBufferSize(bufferSize);
	}

	private void setServerOptions(final Builder builder) {
		val serverOptions = kikahaConf.config().getConfig( "server.undertow.server-options" );
		for ( val entry : serverOptions.entrySet() ) {
			val option = getConfigOption(entry.getValue(), entry.getKey());
			if ( option != null )
				builder.setServerOption(option.getOption(), option.getValue());
		}
	}

	private void setSocketOptions(final Builder builder) {
		val socketOptions = kikahaConf.config().getConfig( "server.undertow.socket-options" );
		for ( val entry : socketOptions.entrySet() ) {
			val option = getConfigOption(entry.getValue(), entry.getKey());
			if ( option != null )
				builder.setSocketOption(option.getOption(), option.getValue());
		}
	}

	private void applyExtraConfigurationsFoundOnClassPath(final Builder builder) {
		for ( val config : extraConfig )
			config.configure(builder);
	}
}
