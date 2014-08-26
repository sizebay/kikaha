package kikaha.core.api;

import java.util.List;
import java.util.Map;

import com.typesafe.config.Config;

public interface AuthenticationConfiguration {

	Map<String, Class<?>> mechanisms();

	Map<String, Class<?>> identityManagers();

	Map<String, Class<?>> notificationReceivers();

	Map<String, Class<?>> securityContextFactories();

	AuthenticationRuleConfiguration defaultRule();

	List<AuthenticationRuleConfiguration> authenticationRules();

	Config config();
}
