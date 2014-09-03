package kikaha.core.api.conf;

import java.util.List;

public interface AuthenticationRuleConfiguration {

	String pattern();

	String identityManager();

	String notificationReceiver();

	String securityContextFactory();

	List<String> mechanisms();

	List<String> expectedRoles();
}
