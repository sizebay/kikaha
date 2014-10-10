package kikaha.core.api.conf;

import com.typesafe.config.Config;

public interface Configuration {

	String applicationName();

	String resourcesPath();

	String welcomeFile();

	Integer port();

	String host();

	AuthenticationConfiguration authentication();

	SSLConfiguration ssl();

	Config config();
}