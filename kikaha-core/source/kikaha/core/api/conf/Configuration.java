package kikaha.core.api.conf;

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

	Config config();
}