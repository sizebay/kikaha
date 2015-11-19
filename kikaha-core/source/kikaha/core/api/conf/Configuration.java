package kikaha.core.api.conf;

import java.util.Map;

import com.typesafe.config.Config;

public interface Configuration {

	String applicationName();

	String resourcesPath();

	String welcomeFile();

	Integer port();
	Integer securePort();
	String host();
	String secureHost();

	AuthenticationConfiguration authentication();

	SSLConfiguration ssl();

	Routes routes();

	Map<String, DatasourceConfiguration> datasources();

	Config config();
}