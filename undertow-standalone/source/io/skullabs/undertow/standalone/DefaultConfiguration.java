package io.skullabs.undertow.standalone;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import io.skullabs.undertow.standalone.api.Configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class DefaultConfiguration implements Configuration {

	final Config config;
	
	@Getter( lazy=true )
	private final Integer port = config().getInt( "undertow.port" );
	
	@Getter( lazy=true )
	private final String host = config().getString( "undertow.host" );
	
	@Getter( lazy=true )
	private final String resourcesPath = config().getString( "undertow.resources-path" );

	public static Configuration loadDefaultConfiguration(){
		Config config = loadDefaultConfig();
		return new DefaultConfiguration(config);
	}
	
	public static Configuration loadConfiguration( String rootPath ) {
		Config defaultConfig = loadDefaultConfig();
		Config root = defaultConfig.getConfig( rootPath );
		Config config = root.withFallback( defaultConfig );
		return new DefaultConfiguration(config);
	}

	private static Config loadDefaultConfig() {
		final Config defaultConfiguration = ConfigFactory.load();
		final Config reference = ConfigFactory.load("META-INF/reference").withFallback( defaultConfiguration );
		return ConfigFactory.load("conf/application").withFallback( reference );
	}
}
