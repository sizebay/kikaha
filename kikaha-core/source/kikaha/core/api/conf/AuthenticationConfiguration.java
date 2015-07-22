package kikaha.core.api.conf;

import java.util.List;
import java.util.Map;

import com.typesafe.config.Config;

public interface AuthenticationConfiguration {

	Map<String, Class<?>> mechanisms();

	Map<String, Class<?>> identityManagers();

	Class<?> securityContextFactory();

	AuthenticationRuleConfiguration defaultRule();

	List<AuthenticationRuleConfiguration> authenticationRules();

	FormAuthConfiguration formAuth();

	Config config();
}
