package io.skullabs.undertow.standalone;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.skullabs.undertow.standalone.api.Configuration;
import io.skullabs.undertow.standalone.api.HandlerTypes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class DefaultConfiguration implements Configuration {

	final Config config;

	@Getter( lazy = true )
	private final Integer port = config().getInt( "undertow.port" );

	@Getter( lazy = true )
	private final String host = config().getString( "undertow.host" );

	@Getter( lazy = true )
	private final String resourcesPath = config().getString( "undertow.resources-path" );

	@Getter( lazy = true )
	private final HandlerTypes defaultHandlerType = getDefaultHandlerType();

	public HandlerTypes getDefaultHandlerType() {
		final String defaultHandlerType = config().getString( "undertow.default-handler-type" );
		final String upperCaseHandlerType = defaultHandlerType.replace( '-', '_' ).toUpperCase();
		return HandlerTypes.valueOf( upperCaseHandlerType );
	}

	public static Configuration loadDefaultConfiguration() {
		Config config = loadDefaultConfig();
		return new DefaultConfiguration( config );
	}

	public static Configuration loadConfiguration( String rootPath ) {
		Config defaultConfig = loadDefaultConfig();
		Config root = defaultConfig.getConfig( rootPath );
		Config config = root.withFallback( defaultConfig );
		return new DefaultConfiguration( config );
	}

	public static Config loadDefaultConfig() {
		final Config defaultConfiguration = ConfigFactory.load();
		final Config reference = ConfigFactory.load( "META-INF/reference" ).withFallback( defaultConfiguration );
		return ConfigFactory.load( "conf/application" ).withFallback( reference );
	}
}
