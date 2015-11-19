package kikaha.core.impl.conf;

import java.util.HashMap;
import java.util.Map;

import kikaha.core.api.conf.AuthenticationConfiguration;
import kikaha.core.api.conf.Configuration;
import kikaha.core.api.conf.DatasourceConfiguration;
import kikaha.core.api.conf.Routes;
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
	final Integer port;
	final String host;
	final Integer securePort;
	final String secureHost;
	final String welcomeFile;
	final AuthenticationConfiguration authentication;
	final SSLConfiguration ssl;
	final Routes routes;
	final Map<String, DatasourceConfiguration> datasources;
	String resourcesPath;
	
	public DefaultConfiguration( final Config config, final String applicationName ) {
		this.config = config;
		this.applicationName = applicationName;
		port = config().getInt( "server.port" );
		host = getConfigString( "server.host" );
		securePort = config().getInt( "server.secure-port" );
		secureHost = getConfigString( "server.secure-host" );
		welcomeFile = getConfigString( "server.welcome-file" );
		authentication = createAuthenticationConfig();
		datasources = createDatasourceConfigurations( config.getConfig( "server.datasources" ) );
		ssl = createSSLConfiguration();
		routes = readRoutes();
	}

	private Map<String, DatasourceConfiguration> createDatasourceConfigurations( Config dsConfig ) {
		final Map<String, DatasourceConfiguration> confs = new HashMap<>();
		final Config defaultConfig = dsConfig.getConfig( "default" );

		for ( final String name : dsConfig.root().keySet() ) {
			final Config foundDsConfig = dsConfig.getConfig( name ).withFallback( defaultConfig );
			final DatasourceConfiguration datasourceConfig = DefaultDatasourceConfiguration.from( name, foundDsConfig );
			if ( datasourceConfig != null )
				confs.put( name, datasourceConfig );
		}
		return confs;
	}

	SSLConfiguration createSSLConfiguration() {
		return new DefaultSSLConfiguration( config().getConfig( "server.ssl" ) );
	}

	AuthenticationConfiguration createAuthenticationConfig() {
		return new DefaultAuthenticationConfiguration( config().getConfig( "server.auth" ) );
	}

	@Override
	public String resourcesPath() {
		if ( resourcesPath == null )
			resourcesPath = getConfigString( "server.resources-path" );
		return resourcesPath;
	}

	public void resourcesPath( final String resourcesPath ) {
		this.resourcesPath = resourcesPath;
	}

	String getConfigString( final String path ) {
		return config().getString( path )
			.replaceFirst( "^\"", "" )
			.replaceFirst( "\"$", "" );
	}

	Routes readRoutes() {
		return new DefaultRoutes( config().getConfig( "server.routes" ) );
	}

	public static DefaultConfiguration loadDefaultConfiguration() {
		final Config config = loadDefaultConfig();
		return new DefaultConfiguration( config, "default" );
	}

	public static DefaultConfiguration loadConfiguration( final String rootPath ) {
		final Config defaultConfig = loadDefaultConfig();
		final Config root = defaultConfig.getConfig( rootPath );
		final Config config = root.withFallback( defaultConfig );
		return new DefaultConfiguration( config, rootPath );
	}

	public static Config loadDefaultConfig() {
		final Config defaultConfiguration = ConfigFactory.load();
		final Config reference = ConfigFactory.load( "META-INF/reference" ).withFallback( defaultConfiguration );
		return ConfigFactory.load( "conf/application" ).withFallback( reference );
	}
}
