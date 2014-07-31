package kikaha.core.api;

import java.util.List;
import java.util.Map;

public interface AuthenticationConfiguration {

	Map<String, Class<?>> mechanisms();

	Map<String, Class<?>> identityManagers();

	Map<String, Class<?>> notificationReceivers();

	AuthenticationRuleConfiguration defaultRule();

	List<AuthenticationRuleConfiguration> authenticationRules();
}
