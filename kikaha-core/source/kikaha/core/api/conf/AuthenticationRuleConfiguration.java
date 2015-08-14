package kikaha.core.api.conf;

import java.util.List;

public interface AuthenticationRuleConfiguration {

	String pattern();

	List<String> exceptionPatterns();

	List<String> identityManagers();

	List<String> mechanisms();

	List<String> expectedRoles();
}
