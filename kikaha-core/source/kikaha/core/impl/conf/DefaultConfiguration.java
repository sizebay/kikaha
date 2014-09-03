package kikaha.core.impl.conf;

import kikaha.core.api.conf.AuthenticationConfiguration;
import kikaha.core.api.conf.Configuration;
import kikaha.core.api.conf.SSLConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class DefaultConfiguration implements Configuration {

	final Config config;
	final String applicationName;

	@Getter( lazy = true )
	private final Integer port = config().getInt( "server.port" );

	@Getter( lazy = true )
	private final String host = config().getString( "server.host" );

	@Getter( lazy = true )
	private final String resourcesPath = config().getString( "server.resources-path" );

	@Getter( lazy = true )
	private final AuthenticationConfiguration authentication = createAuthenticationConfig();

	@Getter( lazy = true )
	private final SSLConfiguration ssl = createSSLConfiguration();

	SSLConfiguration createSSLConfiguration() {
		return new DefaultSSLConfiguration( config().getConfig( "server.ssl" ) );
	}

	AuthenticationConfiguration createAuthenticationConfig() {
		return new DefaultAuthenticationConfiguration( config().getConfig( "server.auth" ) );
	}

	public static Configuration loadDefaultConfiguration() {
		Config config = loadDefaultConfig();
		return new DefaultConfiguration( config, "default" );
	}

	public static Configuration loadConfiguration( String rootPath ) {
		Config defaultConfig = loadDefaultConfig();
		Config root = defaultConfig.getConfig( rootPath );
		Config config = root.withFallback( defaultConfig );
		return new DefaultConfiguration( config, rootPath );
	}

	public static Config loadDefaultConfig() {
		final Config defaultConfiguration = ConfigFactory.load();
		final Config reference = ConfigFactory.load( "META-INF/reference" ).withFallback( defaultConfiguration );
		return ConfigFactory.load( "conf/application" ).withFallback( reference );
	}
}
