package kikaha.core.api;

import com.typesafe.config.Config;

public interface Configuration {

	String applicationName();

	String resourcesPath();

	Integer port();

	String host();

	AuthenticationConfiguration authentication();

	Config config();
}